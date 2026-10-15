package discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends Common {
    private String channelName;
    private List<User> userList;

    public Channel(String channelName) {
        super(System.currentTimeMillis());
        this.channelName = channelName;
        this.userList = new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void setUser(User user) {
        if (!this.userList.contains(user)) {
            this.userList.add(user);
            user.setChannel(this);
        }
    }

    @Override
    public String toString() {
        return "채널명 : " + this.channelName;
    }

    public void deleteUser(UUID userId) {
        this.userList.removeIf(u -> u.getId().equals(userId));
    }
}
