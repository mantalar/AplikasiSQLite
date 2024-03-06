package id.go.kominfo.mantala.aplikasisqlite.dao;

import java.util.List;

import id.go.kominfo.mantala.aplikasisqlite.model.Friend;

public interface FriendDao {
    void insert(Friend friend);
    void update(Friend friend);
    void delete(int id);

    Friend getAFriendById(int id);
    List<Friend> getAllFriends();
}
