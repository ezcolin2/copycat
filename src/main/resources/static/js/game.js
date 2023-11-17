var stompClient;
function connect(roomId) {
    console.log('연결 시작');
    var socket = new SockJS('/copycat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('연결 완료');
        stompClient.subscribe(`/topic/connection/${roomId}`, function (connectionMessage) {
            response = JSON.parse(connectionMessage.body);
            member = response.result;
            console.log(member.id);
            showMessage(response.message, member.id);
        });
        stompClient.send(`/app/connect/${roomId}`);
    });
}

function disconnect(roomId) {
    console.log(`disconnect roomId : ${roomId}`)
    if (stompClient !== null) {
    console.log(`/app/disconnect/${roomId}`)
        stompClient.send(`/app/disconnect/${roomId}`, {});
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}
function showMessage(message, id) {
    var messageElement = document.getElementById(id);
    console.log(messageElement);
    messageElement.innerText = message;
    messageElement.style.display = "block";
    setTimeout(function () {
        messageElement.style.display = "none";
    }, 3000);
}
