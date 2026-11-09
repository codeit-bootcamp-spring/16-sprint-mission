package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.CreatePrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.FindChannelResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("v1/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("userList", userService.findAll());
        return "createChannel";
    }

    @PostMapping("/public")
    public String createPublicChannel(@ModelAttribute CreatePublicChannelRequest request) {
        ChannelResponse response = channelService.createPublicChannel(request);
        return "redirect:/v1/channels/" + response.id();
    }

    @PostMapping("/private")
    public String createPrivateChannel(@ModelAttribute CreatePrivateChannelRequest request) {
        CreatePrivateChannelResponse response = channelService.createPrivateChannel(request);
        return "redirect:/v1/channels/" + response.id();
    }

    @PatchMapping("/update")
    @ResponseBody
    public ResponseEntity<ChannelResponse> updateChannel(@RequestBody UpdateChannelRequest request) {
        ChannelResponse response = channelService.update(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/delete/{channel-id}")
    @ResponseBody
    public void deleteChannel(@PathVariable("channel-id") UUID channelId) {
        channelService.delete(channelId);
    }

    @GetMapping("/user/{user-id}")
    public String getChannels(@PathVariable("user-id") UUID userId,
                              Model model) {
        List<FindChannelResponse> response = channelService.findAllByUserId(userId);
        model.addAttribute("list", response);
        return "userChannelList";
    }

    @GetMapping("/{channelId}")
    public String getChannel(@PathVariable UUID channelId, Model model) {
        model.addAttribute("channel", channelService.find(channelId));
        return "showChannel";
    }
}
