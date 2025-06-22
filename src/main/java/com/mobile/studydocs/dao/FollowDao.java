package com.mobile.studydocs.dao;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
//import com.mobile.studydocs.model.dto.response.FollowingResponse;
import com.mobile.studydocs.model.dto.response.FollowingResponse;
import com.mobile.studydocs.model.entity.Follower;
import com.mobile.studydocs.model.entity.Following;
import com.mobile.studydocs.model.entity.User;
import com.mobile.studydocs.model.enums.FollowType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowDao {
    private static final String FOLLOWERS_COLLECTION = "followers";
    private static final String FOLLOWINGS_COLLECTION = "followings";
    private static final String USERS_COLLECTION = "users";
    private final Firestore firestore;

    private FollowingResponse getFollowing(DocumentReference followingRef, DocumentReference targetRef) throws ExecutionException, InterruptedException {
        Following following = followingRef.get().get().toObject(Following.class);
        if (following != null) {
            DocumentSnapshot targetSnapShot = targetRef.get().get();
            if (targetSnapShot.exists() && targetSnapShot.getId().equals("users")) {
                User user = targetSnapShot.toObject(User.class);
                assert user != null;
                return FollowingResponse.builder()
                        .followingId(followingRef.getId())
                        .targetType(FollowType.USER)
                        .avatarUrl(user.getAvatarUrl())
                        .name(user.getFullName())
                        .targetId(user.getUserId())
                        .notifyEnables(following.isNotifyEnable())
                        .build();
            } else {
                throw new RuntimeException("Loại theo dõi không hợp lệ");
            }
        } else {
            throw new RuntimeException("Lỗi khi lấy Following");
        }
    }

    /**
     * Thêm follow relationship
     */
    public FollowingResponse addFollower(String userId, FollowType targetType, String targetId) {
        try {
            DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(userId);
            DocumentReference followerRef = addFollowerList(targetId, targetType.toString(), userRef);

            //Tạo following
            Following following = Following.builder()
                    .targetRef(firestore.collection(targetType.getValue()).document(targetId))
                    .followerRef(followerRef)
                    .notifyEnable(true)
                    .build();
            DocumentReference followingRef = userRef.collection(FOLLOWINGS_COLLECTION).add(following).get();
            return getFollowing(followingRef, following.getTargetRef());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Theo dõi không thành công");
        }
    }

    /**
     * Unfollow
     */
    public void removeFollower(String userId, String followingId) {
        try {
            DocumentReference followingRef = firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(FOLLOWINGS_COLLECTION)
                    .document(followingId);
            Following following = followingRef.get().get().toObject(Following.class);

            //xóa FollowerRef
            if (following == null) {
                throw new RuntimeException("Hủy theo dõi không thành công");
            }
            following.getFollowerRef().update("followerRefs", FieldValue.arrayUnion(followingRef));
            followingRef.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Hủy theo dõi không thành công");
        }
    }

    /**
     * Bật/tắt thông báo cho một following
     */
    public void toggleNotification(String userId, String followingId, boolean notifyEnable) {
        try {
            firestore.collection(USERS_COLLECTION).document(userId).collection(FOLLOWINGS_COLLECTION).document(followingId).update("notifyEnable", notifyEnable);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Thay đôi trạng thái thông báo không thành công");
        }
    }

    /**
     * Helper method để cập nhật danh sách followers
     */
    private DocumentReference addFollowerList(String targetId, String targetType, DocumentReference followerRef) {
        try {
            DocumentSnapshot documentSnapshot = firestore.collection(FOLLOWERS_COLLECTION)
                    .document(targetType + "-" + targetId)
                    .get()
                    .get();
            if (documentSnapshot.exists()) {
                documentSnapshot.getReference().update("followerRefs", FieldValue.arrayUnion(followerRef));
                return documentSnapshot.getReference();
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("targetId", targetId);
                data.put("targetType", targetType);
                List<DocumentReference> followers = new ArrayList<>();
                followers.add(followerRef);
                data.put("followerRefs", followers);
                DocumentReference newDocRef = firestore.collection(FOLLOWERS_COLLECTION).document(targetType + "-" + targetId);
                newDocRef.set(data);
                return newDocRef;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<User> getFollowers(String targetId, FollowType targetType) {
        List<User> result = new ArrayList<>();
        try {
            Follower follower = firestore
                    .collection(FOLLOWERS_COLLECTION)
                    .document(targetType.toString() + "-" + targetId)
                    .get()
                    .get()
                    .toObject(Follower.class);
            if (follower != null && follower.getFollowerRefs() != null) {
                for (DocumentReference followerRef : follower.getFollowerRefs()) {
                    if (followerRef != null) {
                        try {
                            DocumentSnapshot docSnap = followerRef.get().get();
                            if (docSnap.exists()) {
                                User user = docSnap.toObject(User.class);
                                if (user != null) result.add(user);
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getFollowers: {}", e.getMessage());
        }
        return result;
    }

    public List<FollowingResponse> getFollowings(String userId) {
        try {
            List<FollowingResponse> result = new ArrayList<>();
            firestore.collection(USERS_COLLECTION).document(userId).collection(FOLLOWINGS_COLLECTION)
                    .get().get()
                    .forEach(doc -> {
                        Following following = doc.toObject(Following.class);
                        try {
                            DocumentReference ref = following.getTargetRef();
                            DocumentSnapshot snapshot = ref.get().get();
                            if (snapshot.exists() && "users".equals(ref.getParent().getId())) {
                                User user = snapshot.toObject(User.class);
                                if (user != null) {
                                    result.add(FollowingResponse.builder()
                                            .followingId(doc.getId())
                                            .avatarUrl(user.getAvatarUrl())
                                            .name(user.getFullName())
                                            .targetId(user.getUserId())
                                            .targetType(FollowType.USER)
                                            .notifyEnables(following.isNotifyEnable())
                                            .build());
                                }
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<String> getFCMTokens(String userId, String targetId, String targetType) {
        try {
            DocumentReference userRef = firestore.collection(USERS_COLLECTION)
                    .document(userId);
            if (!userRef.collection(FOLLOWINGS_COLLECTION)
                    .whereEqualTo("targetId", targetId)
                    .whereEqualTo("targetType", targetType)
                    .whereEqualTo("notifyEnable", true)
                    .get().get().isEmpty()) {
                return Objects.requireNonNull(userRef.get().get().toObject(User.class)).getFcmTokens();
            }
        } catch (Exception e) {
            log.error("Error getting users need notify for target {}:{} - {}",
                    targetType, targetId, e.getMessage());
        }
        return Collections.emptyList();
    }

}
