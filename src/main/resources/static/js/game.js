var stompClient;
var clock;
function connect(roomId) {
    console.log('연결 시작');
    var socket = new SockJS('/copycat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('연결 완료');
        stompClient.subscribe(`/topic/connection/${roomId}`, function (connectionMessage) {
            response = JSON.parse(connectionMessage.body);
            member = response.result;
            console.log(member.memberId);
            showMessage(response.message, member.memberId);
        });
        stompClient.subscribe(`/topic/message/${roomId}`, function (errorMessage) {
            response = JSON.parse(errorMessage.body);
            showMessage(response.message, response.memberId)
        });
        stompClient.subscribe(`/user/topic/game/${roomId}`, function (turnState) {
            response = JSON.parse(turnState.body);
            if (response.state=="OFFENSE"){
                setTimeout(function () {
//                   const imageSrc = captureAndDisplayFrame(`video-${response.memberId}`)
                   sendImage(`video-${response.memberId}`, roomId);
//                   stompClient.send(`/app/offense/${roomId}`, {}, "asdf");
                   console.log('이미지 정보 전송');
                }, 10000);
            }
            else if (response.state=="DEFENSE"){
                setTimeout(function () {
                   stompClient.send(`/app/defense/${roomId}`, {}, 10);
                   console.log('점수 정보 전송');
                }, 10000);
            }
        });
        stompClient.subscribe(`/topic/game/${roomId}`, function (gameInfo) {
            response = JSON.parse(gameInfo.body);
            console.log(response);
            if (response.turnState=="OFFENSE"){
                video = document.querySelector(`#video-${response.memberId}`);
                video.className = 'offense';
                setTimeout(function () {
                    video = document.querySelector(`#video-${response.memberId}`);
                    if (video.className=='offense'){
                    video.className = '';
//                    captureAndDisplayFrame(`video-${response.memberId}`)
                    }
                }, 10000);
                console.log(video)
                showTimer();
            } else{
                video = document.querySelector(`#video-${response.memberId}`);
                video.className = 'defense';
                // 이미지 띄우기
                const image = document.createElement("img");
                const timestamp = new Date().getTime();
                image.src = response.image + "?time=" + timestamp;
                if (response.memberId == response.creatorId){
                    // 자신이 아닌 유저 화면 멈추기
                    displayImage(image, `video-${response.participantId}`);
                } else{
                    // 자신이 아닌 유저 화면 멈추기
                    displayImage(image, `video-${response.creatorId}`);

                }
                setTimeout(function () {
                    if (video.className=='defense'){
                       video.className = '';
                    }
//                   captureAndDisplayFrame(`video-${response.memberId}`)
                }, 10000);

                console.log(video)
                showTimer();
            }
        });
        stompClient.send(`/app/connect/${roomId}`);
    });
    const btn = document.querySelector("#start")
    btn.addEventListener('click', function (event){
        stompClient.send(`/app/start/${roomId}`);
    })
}

function disconnect(roomId) {
    console.log(`disconnect roomId : ${roomId}`)
    if (stompClient !== null) {
    console.log(`/app/disconnect/${roomId}`)
        stompClient.send(`/app/disconnect/${roomId}`);
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
function showTimer(){
    var timer= document.querySelector('#timer');
    var wrapper = document.querySelector('.circleProgress_wrapper')
    wrapper.style = "visibility:visible";
    var time=9;
    resetAnimation();
    clearInterval(clock);
    clock = setInterval(function(){
        timer.innerHTML=time;
        time--;
        if(time<0) {
            timer.innerHTML=10;
            wrapper.style = "visibility:hidden";
            clearInterval(clock);
            return;
        }
    },1000)
}
function resetAnimation(){
    // 출처 : https://velog.io/@dnrgus1127/CSS-keyFrames-%EC%95%A0%EB%8B%88%EB%A9%94%EC%9D%B4%EC%85%98-%EC%B4%88%EA%B8%B0%ED%99%94
    var left = document.querySelector('.leftcircle');
    console.log(left)
    left.style.animation = "none";
    void left.offsetWidth;
    left.style.animation = null;

    var right = document.querySelector('.rightcircle');
    right.style.animation = "none";
    void right.offsetWidth;
    right.style.animation = null;
}

function displayImage(image, videoId) {
    var video = document.getElementById(videoId);
    var videoRect = video.getBoundingClientRect();

    var imgContainer = document.createElement('div');
    imgContainer.style.position = 'absolute';
    imgContainer.style.top = videoRect.top + 'px';
    imgContainer.style.left = videoRect.left + 'px';

    image.width = videoRect.width;
    image.height = videoRect.height;

    imgContainer.appendChild(image);

    document.body.appendChild(imgContainer);

    setTimeout(function() {
        document.body.removeChild(imgContainer);
    }, 10000);
}
function sendImage(videoId, roomId){
   var video = document.getElementById(videoId);
   console.log(video);

   // 캡처된 프레임을 그릴 캔버스 생성
   var canvas = document.createElement('canvas');
   canvas.width = video.videoWidth;
   canvas.height = video.videoHeight;

   // 캔버스에 비디오 현재 프레임을 그리기
   var ctx = canvas.getContext('2d');
   ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

   // 캔버스의 이미지 데이터를 가져와서 이미지로 변환
   var capturedImage = new Image();
   const base64Image = canvas.toDataURL('image/png').split(',')[1];;
   console.log(capturedImage);
   // binary 변환
//   var imageData = canvas.toDataURL('image/png').replace(/^data:image\/(png|jpg);base64,/, '');
//   var binaryData = atob(imageData);

   //서버에 데이터 보내기
   const data = {
       roomId: roomId,
       base64Image: base64Image
   };

   fetch("http://localhost/api/images", {
       method: 'POST',
       headers: {
           'Content-Type': 'application/json',
       },
       body: JSON.stringify(data),
   })
   .then(response => response.json())
   .then(data => {
       if (data.code==201){
          stompClient.send(`/app/offense/${roomId}`);
       }
       console.log(data);
   })
   .catch((error) => {
       console.error('Error:', error);
   });
}
