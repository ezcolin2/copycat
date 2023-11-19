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
                   const imageSrc = captureAndDisplayFrame(`video-${response.memberId}`)
                   stompClient.send(`/app/offense/${roomId}`, {}, "asdf");
                   console.log('이미지 정보 전송');
                   console.log(imageSrc);
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
                   video.className = '';
                   captureAndDisplayFrame(`video-${response.memberId}`)
                }, 10000);
                console.log(video)
                showTimer();
            } else{
                video = document.querySelector(`#video-${response.memberId}`);
                video.className = 'defense';
                setTimeout(function () {
                   video.className = '';
//                   captureAndDisplayFrame(`video-${response.memberId}`)
                }, 10000);

                console.log(video)
            }
            showTimer();
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
    var clock = setInterval(function(){
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

function captureAndDisplayFrame(videoId) {
    // 비디오 엘리먼트 참조
    var video = document.getElementById(videoId);
    console.log(video);

    // 비디오 메타데이터 로드 후 콜백 함수 등록
    // 현재 비디오 시간을 가져와서 프레임 캡처

    // 캡처된 프레임을 그릴 캔버스 생성
    var canvas = document.createElement('canvas');
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    // 캔버스에 비디오 현재 프레임을 그리기
    var ctx = canvas.getContext('2d');
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    // 캔버스의 이미지 데이터를 가져와서 이미지로 변환
    var capturedImage = new Image();
    capturedImage.src = canvas.toDataURL('image/png');
    console.log(capturedImage);

    // 이미지를 표시하는 함수 호출
    displayImage(capturedImage, videoId);
    return capturedImage.src;
}

function displayImage(image, videoId) {
    // 이미지를 video 태그의 크기와 동일하게 만들기
    var video = document.getElementById(videoId);
    var videoRect = video.getBoundingClientRect();

    // 이미지를 video 태그의 바로 위에 표시
    var imgContainer = document.createElement('div');
    imgContainer.style.position = 'absolute';
    imgContainer.style.top = videoRect.top + 'px';
    imgContainer.style.left = videoRect.left + 'px';

    // 이미지 크기 설정
    image.width = videoRect.width;
    image.height = videoRect.height;

    imgContainer.appendChild(image);

    // 문서에 이미지 컨테이너 추가
    document.body.appendChild(imgContainer);

    // 3초 후에 이미지 컨테이너 제거 (종료 시간 조절 가능)
    setTimeout(function() {
        document.body.removeChild(imgContainer);
    }, 10000);
}