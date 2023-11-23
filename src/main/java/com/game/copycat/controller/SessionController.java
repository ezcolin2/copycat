package com.game.copycat.controller;


import com.game.copycat.domain.Game;
import com.game.copycat.domain.Member;
import com.game.copycat.domain.PrincipalDetails;
import com.game.copycat.domain.Room;
import com.game.copycat.dto.RoomRequest;
import com.game.copycat.service.GameService;
import com.game.copycat.service.RoomService;
import io.openvidu.java.client.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.WebSocket;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@CrossOrigin(origins="*")
public class SessionController {
    @Autowired
    private RoomService roomService;
    @Autowired
    private GameService gameService;

    // OpenVidu object as entrypoint of the SDK
    private OpenVidu openVidu;

    // Collection to pair session names and OpenVidu Session objects
    private Map<String, Session> mapSessions = new ConcurrentHashMap<>();
    // Collection to pair session names and tokens (the inner Map pairs tokens and
    // role associated)
    private Map<String, Map<String, OpenViduRole>> mapSessionNamesTokens = new ConcurrentHashMap<>();

    // URL where our OpenVidu server is listening
    @Value("${openvidu.url}")
    private String OPENVIDU_URL;
    // Secret shared with our OpenVidu server
    @Value("${openvidu.secret}")
    private String SECRET;
    @PostConstruct
    public void init() {
        System.out.println("secret = " + SECRET);
        System.out.println("openvidu = " + OPENVIDU_URL);
        this.openVidu = new OpenVidu(OPENVIDU_URL, SECRET);
    }

    @GetMapping("/session/{id}")
    public String enterSession(
            @PathVariable("id") String roomId,
            Model model,
            Authentication authentication
    ) {
        // Role associated to this user
        OpenViduRole role = OpenViduRole.PUBLISHER;

        // Optional data to be passed to other users when this user connects to the
        // video-call. In this case, a JSON with the value we stored in the HttpSession
        // object on login
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();
        String serverData = "{\"serverData\": \"" + member.getMemberId().toString() + "\"}";

        // Build connectionProperties object with the serverData and the role
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();
        // Session already exists
        try {
//            Game game = gameService.enterGame(roomId);
            Room room = roomService.findById(roomId);
            // 방이 꽉 찼다면 못 들어감
            if (room.getCurrentNum() >= 2) {
                return "redirect:/rooms";

            }
//            model.addAttribute("game", game);
            // Generate a new token with the recently created connectionProperties
            String token = this.mapSessions.get(roomId).createConnection(connectionProperties).getToken();

            // Update our collection storing the new token
            this.mapSessionNamesTokens.get(roomId).put(token, role);

            // Add all the needed attributes to the template
            model.addAttribute("room", room);
            model.addAttribute("roomId", roomId);
            model.addAttribute("token", token);
            model.addAttribute("nickName", member.getNickname());
            model.addAttribute("userName", member.getMemberId());
            // Return session.html template
            return "game";

        } catch (Exception e) {
            e.printStackTrace();
            // If error just return dashboard.html template
            return "redirect:/rooms";
        }
    }
    @PostMapping("/session")
    public String createSession(
            @ModelAttribute @Valid RoomRequest request,
            Model model,
            Authentication authentication
    ) {

        // Role associated to this user
        OpenViduRole role = OpenViduRole.PUBLISHER;

        // Optional data to be passed to other users when this user connects to the
        // video-call. In this case, a JSON with the value we stored in the HttpSession
        // object on login
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        Member member = principalDetails.getMember();
        String serverData = "{\"serverData\": \"" + member.getMemberId().toString() + "\"}";

        // Build connectionProperties object with the serverData and the role
        ConnectionProperties connectionProperties = new ConnectionProperties.Builder().type(ConnectionType.WEBRTC)
                .role(role).data(serverData).build();

            // New session
            try {

                Room room = roomService.createRoom(request);
//                Game game = gameService.createGame(room.getId(), room.getRoomName(), member.getId());
                model.addAttribute("room", room);
                model.addAttribute("game", room);
                // Create a new OpenVidu Session
                Session session = this.openVidu.createSession();
                // Generate a new token with the recently created connectionProperties
                String token = session.createConnection(connectionProperties).getToken();

                // Store the session and the token in our collections
                this.mapSessions.put(room.getId(), session);
                this.mapSessionNamesTokens.put(room.getId(), new ConcurrentHashMap<>());
                this.mapSessionNamesTokens.get(room.getId()).put(token, role);

                // Add all the needed attributes to the template
                model.addAttribute("roomId", room.getId());
                model.addAttribute("token", token);
                model.addAttribute("nickName", member.getNickname());
                model.addAttribute("userName", member.getMemberId());

                // Return session.html template

                return "game";


            } catch (Exception e) {
                e.printStackTrace();
                // If error just return dashboard.html template
                model.addAttribute("username", "");
                return "redirect:/rooms";
            }
        }
    @PostMapping("/leave-session")
    public String removeUser(@RequestParam(name = "roomId") String roomId,
                             @RequestParam(name = "token") String token, Model model, HttpSession httpSession) throws Exception {

        System.out.println("Removing user | sessioName=" + roomId + ", token=" + token);

        // If the session exists ("TUTORIAL" in this case)
        if (this.mapSessions.get(roomId) != null && this.mapSessionNamesTokens.get(roomId) != null) {

            // If the token exists
            if (this.mapSessionNamesTokens.get(roomId).remove(token) != null) {
                // User left the session
                if (this.mapSessionNamesTokens.get(roomId).isEmpty()) {
                    // Last user left: session must be removed
                    this.mapSessions.remove(roomId);
                    // session과 함께 Room도 삭제
                    roomService.deleteRoom(roomId);
                }
                return "redirect:/rooms";

            } else {
                // The TOKEN wasn't valid
                System.out.println("Problems in the app server: the TOKEN wasn't valid");
                return "redirect:/rooms";
            }

        } else {
            // The SESSION does not exist
            System.out.println("Problems in the app server: the SESSION does not exist");
            return "redirect:/rooms";
        }
    }

}
