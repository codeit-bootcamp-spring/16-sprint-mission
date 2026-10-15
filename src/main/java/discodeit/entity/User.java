package discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends Common {
    private String userName;
    private List<Channel> channelList;

    public User(String userName) {
        super(System.currentTimeMillis());
        this.userName = userName;
        this.channelList = new ArrayList<>();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    @Override
    public String toString() {
        return "유저명 : " + this.userName;
    }

    public void setChannel(Channel channel) {
        if(!this.channelList.contains(channel)) {
            this.channelList.add(channel);
            channel.setUser(this);
        }
    }

    public void deleteChannel(UUID channelId) {
        this.channelList.removeIf(c -> c.getId().equals(channelId));
    }
}
