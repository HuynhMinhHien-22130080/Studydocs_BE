package com.mobile.studydocs.dao;

import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Đối tượng truy cập dữ liệu (DAO) để quản lý mối quan hệ người dùng (người theo dõi/đang theo dõi) trong Firestore.
 * Sử dụng chiến lược phân mảnh (sharding) để xử lý hiệu quả các tập hợp lớn.
 */
@Component
public class UserDao {
    // Tên các collection
    private static final String USER_COLLECTION = "users";
    private static final String FOLLOWER_COLLECTION = "followers";
    private static final String FOLLOWING_COLLECTION_PREFIX = "following_";

    // Tên các trường dữ liệu
    private static final String FOLLOWER_ID_FIELD = "followerId";
    private static final String FOLLOWER_SHARD_SIZE_FIELD = "followerShardSize";
    private static final String FOLLOWING_SHARD_SIZE_FIELD = "followingShardSize";
    private static final String NOTIFY_ENABLE_FIELD = "notifyEnable";

    // Giá trị mặc định
    private static final boolean DEFAULT_NOTIFY_ENABLE = true;
    private static final int INITIAL_SHARD_INDEX = 0;

    private final int shardLimit;
    private final Firestore firestore;

    public UserDao(@Value("${user.shard.limit}") int shardLimit, Firestore firestore) {
        this.shardLimit = shardLimit;
        this.firestore = firestore;
    }

    /**
     * Lấy tham chiếu đến tài liệu người dùng trong Firestore
     * @param userId ID của người dùng
     * @return DocumentReference tương ứng với người dùng
     */
    public DocumentReference getUserRef(String userId) {
        return firestore.collection(USER_COLLECTION).document(userId);
    }

    private String getFollowerShardSize(String userId) throws ExecutionException, InterruptedException {
        return getUserRef(userId).get().get().getString(FOLLOWER_SHARD_SIZE_FIELD);
    }

    private String getFollowingShardSize(String userId) throws ExecutionException, InterruptedException {
        return getUserRef(userId).get().get().getString(FOLLOWING_SHARD_SIZE_FIELD);
    }

    /**
     * Tạo quan hệ theo dõi giữa hai người dùng
     * @param followeeId ID của người bị theo dõi
     * @param followerId ID của người đang theo dõi
     * @return true nếu thực hiện thành công, false nếu có lỗi
     */
    public boolean addFollow(String followeeId, String followerId) {
        try {
            DocumentReference followeeRef = getUserRef(followeeId);
            int currentShardIndex = Integer.parseInt(getFollowerShardSize(followeeId));
            DocumentReference followerShardRef = followeeRef.collection(FOLLOWER_COLLECTION)
                    .document(String.valueOf(currentShardIndex));

            // Lấy danh sách người theo dõi hiện tại
            List<String> currentFollowers = getCurrentFollowers(followerShardRef);

            // Tạo phân mảnh mới nếu phân mảnh hiện tại đã đầy
            if (currentFollowers.size() >= shardLimit) {
                currentShardIndex++;
                followerShardRef = followeeRef.collection(FOLLOWER_COLLECTION)
                        .document(String.valueOf(currentShardIndex));
                followeeRef.update(FOLLOWER_SHARD_SIZE_FIELD, String.valueOf(currentShardIndex));
            }

            // Thêm người theo dõi
            followerShardRef.update(FOLLOWER_ID_FIELD, FieldValue.arrayUnion(followerId)).get();

            // Tạo tài liệu đang theo dõi cho người theo dõi
            createFollowingDocument(followerId, followeeId);

            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> getCurrentFollowers(DocumentReference shardRef) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = shardRef.get().get();
        List<String> followers = (List<String>) snapshot.get(FOLLOWER_ID_FIELD);
        return followers != null ? followers : new ArrayList<>();
    }

    private void createFollowingDocument(String followerId, String followeeId) throws ExecutionException, InterruptedException {
        DocumentReference followerRef = getUserRef(followerId);
        int followingShardIndex = Integer.parseInt(getFollowingShardSize(followerId));
        DocumentReference followingDocRef = followerRef
                .collection(FOLLOWING_COLLECTION_PREFIX + followingShardIndex)
                .document(followeeId);

        // Nếu chưa tồn tại thì tạo mới
        if (!followingDocRef.get().get().exists()) {
            Map<String, Object> followingData = new HashMap<>();
            followingData.put(NOTIFY_ENABLE_FIELD, DEFAULT_NOTIFY_ENABLE);
            followingDocRef.set(followingData).get();
        }
    }

    /**
     * Xóa quan hệ theo dõi giữa hai người dùng
     * @param followeeId ID của người bị bỏ theo dõi
     * @param followerId ID của người bỏ theo dõi
     * @return true nếu thực hiện thành công, false nếu có lỗi
     */
    public boolean removeFollow(String followeeId, String followerId) {
        try {
            removeFollower(followeeId, followerId);
            removeFollowing(followerId, followeeId);
            return true;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void removeFollower(String followeeId, String followerId) throws ExecutionException, InterruptedException {
        DocumentReference followeeRef = getUserRef(followeeId);
        int followerShardCount = Integer.parseInt(getFollowerShardSize(followeeId));
        int remainingFollowerShards = 0;

        for (int shardIndex = INITIAL_SHARD_INDEX; shardIndex <= followerShardCount; shardIndex++) {
            DocumentReference shardDoc = followeeRef.collection(FOLLOWER_COLLECTION)
                    .document(String.valueOf(shardIndex));

            if (shardDoc.get().get().exists()) {
                // Xóa người theo dõi khỏi phân mảnh
                shardDoc.update(FOLLOWER_ID_FIELD, FieldValue.arrayRemove(followerId)).get();

                // Xóa tài liệu phân mảnh nếu rỗng
                if (isShardEmpty(shardDoc)) {
                    shardDoc.delete().get();
                } else {
                    remainingFollowerShards++;
                }
            }
        }

        // Cập nhật số lượng phân mảnh còn lại
        followeeRef.update(FOLLOWER_SHARD_SIZE_FIELD, String.valueOf(remainingFollowerShards)).get();
    }

    private boolean isShardEmpty(DocumentReference shardDoc) throws ExecutionException, InterruptedException {
        Map<String, Object> data = shardDoc.get().get().getData();
        return data == null || !data.containsKey(FOLLOWER_ID_FIELD) ||
                ((List<?>) data.get(FOLLOWER_ID_FIELD)).isEmpty();
    }

    private void removeFollowing(String followerId, String followeeId) throws ExecutionException, InterruptedException {
        DocumentReference followerRef = getUserRef(followerId);
        int followingShardCount = Integer.parseInt(getFollowingShardSize(followerId));
        int remainingFollowingShards = 0;

        for (int shardIndex = INITIAL_SHARD_INDEX; shardIndex <= followingShardCount; shardIndex++) {
            String collectionName = FOLLOWING_COLLECTION_PREFIX + shardIndex;
            DocumentReference followingDoc = followerRef.collection(collectionName).document(followeeId);

            // Xóa tài liệu theo dõi nếu tồn tại
            if (followingDoc.get().get().exists()) {
                followingDoc.delete().get();
            }

            // Kiểm tra còn tài liệu nào trong phân mảnh
            if (hasDocumentsInCollection(followerRef.collection(collectionName))) {
                remainingFollowingShards++;
            }
        }

        // Cập nhật số lượng phân mảnh còn lại
        followerRef.update(FOLLOWING_SHARD_SIZE_FIELD, String.valueOf(remainingFollowingShards)).get();
    }

    private boolean hasDocumentsInCollection(CollectionReference collection) throws ExecutionException, InterruptedException {
        return collection.listDocuments().iterator().hasNext();
    }
}
