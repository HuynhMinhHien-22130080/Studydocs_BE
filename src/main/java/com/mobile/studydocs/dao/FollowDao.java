package com.mobile.studydocs.dao;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
//import com.mobile.studydocs.model.dto.response.FollowingResponse;
import com.mobile.studydocs.exception.ResourceNotFoundException;
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


    /**
     * Thêm follow relationship
     */
    public void addFollower(String userId, FollowType targetType, String targetId) {
        try {
            DocumentReference userRef = firestore.collection(USERS_COLLECTION).document(userId);
            DocumentReference followerRef = addFollowerList(targetId, targetType.toString(), userRef);

            //Tạo following
            Following following = Following.builder()
                    .targetRef(firestore.collection(targetType.getValue()).document(targetId))
                    .followerRef(followerRef)
                    .notifyEnable(true)
                    .build();
            userRef.collection(FOLLOWINGS_COLLECTION).add(following).get();
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
        String docId = targetType + "-" + targetId;
        DocumentReference docRef = firestore.collection(FOLLOWERS_COLLECTION).document(docId);

        try {
            DocumentSnapshot documentSnapshot = docRef.get().get();
            if (documentSnapshot.exists()) {
                docRef.update("followerRefs", FieldValue.arrayUnion(followerRef));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("targetId", targetId);
                data.put("targetType", targetType);
                List<DocumentReference> followers = new ArrayList<>();
                followers.add(followerRef);
                data.put("followerRefs", followers);
                docRef.set(data).get();
            }
            return docRef;
        } catch (Exception e) {
            log.error("❌ Lỗi khi tạo/cập nhật followers/{}: {}", docId, e.getMessage(), e);
            throw new RuntimeException("Không thể tạo hoặc cập nhật followers list", e);
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

    public List<String> getFCMTokensForUser(String userId, String targetId, FollowType targetType) {
        try {
            DocumentReference userRef = firestore.collection(USERS_COLLECTION)
                    .document(userId);
            DocumentReference targetRef = firestore.collection(targetType.getValue()).document(targetId);

            if (!userRef.collection(FOLLOWINGS_COLLECTION)
                    .whereEqualTo("targetRef", targetRef)
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

    public String getFollowingId(String userId, String targetId, FollowType type) {
        List<FollowingResponse> followingResponses = getFollowings(userId);
        return followingResponses.stream()
                .filter(f -> f.targetId().equals(targetId) && f.targetType() == type)
                .map(FollowingResponse::followingId)
                .findFirst()
                .orElse(null);
    }

    public Map<String, List<String>> getFCMTokens(String targetId, FollowType targetType) {
        try {
            String followerId = targetType + "-" + targetId;
            Follower follower = firestore.collection(FOLLOWERS_COLLECTION)
                    .document(followerId).get().get().toObject(Follower.class);
            if (follower == null) {
                throw new ResourceNotFoundException("Người theo dõi", "targetId", targetId);
            }
            Map<String, List<String>> result = new HashMap<>();
            for (DocumentReference followerRef : follower.getFollowerRefs()) {
                if (followerRef != null) {
                    DocumentSnapshot docSnap = followerRef.get().get();
                    if (docSnap.exists()) {
                        result.put(docSnap.getId(), getFCMTokensForUser(docSnap.getId(), targetId, targetType));
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Error getting followers for target {}:{} - {}",
                    targetType, targetId, e.getMessage());
            throw new RuntimeException("Không tìm thấy người theo dõi");
        }

    }

}
