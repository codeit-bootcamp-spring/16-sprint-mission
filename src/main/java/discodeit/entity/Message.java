package discodeit.entity;

public class Message extends Common {

    private User user;
    private Channel channel;
    private String comment;

    public Message(User user, Channel channel, String comment) {
        super(System.currentTimeMillis());
        this.user = user;
        this.channel = channel;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "작성한 메세지 : " + this.comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }
}
