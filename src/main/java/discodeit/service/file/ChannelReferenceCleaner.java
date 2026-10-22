package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;

import java.util.*;

public final class ChannelReferenceCleaner {
    public ChannelReferenceCleaner() {}

    public static void clean(Collection<?> roots, UUID deletedChannelId) {
        Set<Object> visited = Collections.newSetFromMap(new IdentityHashMap<>());

        for (Object root : roots) {
            visit(root, deletedChannelId, visited);
        }
    }

    public static void visit(Object object, UUID channelId, Set<Object> visited) {
        if (object == null || !visited.add(object)) {
            return;
        }

        if (object instanceof User user) {
            // 삭제한 채널과 연결을 먼저 끊음
            user.deleteChannel(channelId);

            for (Channel channel : user.getChannelList()) {
                visit(channel, channelId, visited);
            }
        } else if (object instanceof Channel channel) {
            for (User user : channel.getUserList()) {
                visit(user, channelId, visited);
            }
        } else if (object instanceof Message message) {
            visit(message.getUser(), channelId, visited);
            visit(message.getChannel(), channelId, visited);
        }
    }
}
