package com.mobile.studydocs.dao;

import com.google.cloud.firestore.*;
import com.mobile.studydocs.model.dto.response.FollowingResponse;
import com.mobile.studydocs.model.entity.Following;
import com.mobile.studydocs.model.entity.User;
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
    public void addFollower(String userId, String targetType, String targetId) {
        try {
            DocumentReference userRef = firestore.collection(FOLLOWERS_COLLECTION).document(userId);
            DocumentReference followerRef = updateFollowersList(targetId, targetType, userRef, true);

            //Tạo following
            Following following = Following.builder()
                    .targetRef(firestore.collection(targetType).document(targetId))
                    .followerRef(followerRef)
                    .notifyEnable(true)
                    .build();
            userRef.collection(FOLLOWINGS_COLLECTION).add(following);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Theo dõi không thành công");
        }
    }

    /**
     * Unfollow
     */
    public void removeFollower(String userId, String targetId, String targetType) {
        try {
            // Xóa từ collection followings
            DocumentReference targetRef = firestore.collection(USERS_COLLECTION).document(targetId);
            firestore.collection(USERS_COLLECTION).document(userId).collection(FOLLOWINGS_COLLECTION).whereEqualTo("targetRef", targetRef).get().get().getDocuments().forEach(doc -> {
                Following following = doc.toObject(Following.class);
                updateFollowersList(targetId, targetType, following.getFollowerRef(), false);
                doc.getReference().delete();
            });
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
    private DocumentReference updateFollowersList(String targetId, String targetType, DocumentReference followerRef, boolean isAdd) {
        try {
            QuerySnapshot queryDocumentSnapshots = firestore.collection(FOLLOWERS_COLLECTION)
                    .whereEqualTo("targetId", targetId)
                    .whereEqualTo("targetType", targetType)
                    .get().get();
            if (!queryDocumentSnapshots.isEmpty()) {
                QueryDocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                doc.getReference().update("followers", isAdd ? FieldValue.arrayUnion(followerRef) : FieldValue.arrayRemove(followerRef));
                return doc.getReference();
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("targetId", targetId);
                data.put("targetType", targetType);
                List<DocumentReference> followers = new ArrayList<>();
                if (isAdd) followers.add(followerRef);
                data.put("followers", followers);
                DocumentReference newDocRef = firestore.collection(FOLLOWERS_COLLECTION).document();
                newDocRef.set(data);
                return newDocRef;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    public List<User> getFollowers(String targetId, String targetCollection) {
        try {
            return firestore.collection(FOLLOWERS_COLLECTION)
                    .whereEqualTo("targetId", targetId)
                    .whereEqualTo("targetCollection", targetCollection)
                    .get()
                    .get()
                    .toObjects(User.class);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
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
