// posenet.js
// 코드 참조 : https://gyong0117.tistory.com/153
const btn = document.querySelector("#draw")
btn.addEventListener('click', function (event){
    const videos = document.querySelectorAll("video");
    const canvasList = document.querySelectorAll('canvas');
    // 기존 존재하는 canvas 모두 삭제
    if (canvasList.length > 0) {
        canvasList.forEach(canvas => {
            canvas.remove();
        });
    }

    videos.forEach(function(video){
        make_canvas(video);
//        video.className='offense'
    })
})
function make_canvas(video){
    video.width=video.offsetWidth;
    video.height=video.offsetHeight;

    // canvas 생성
    const canvas = document.createElement("canvas");
    const context = canvas.getContext("2d");

    // canvas를 video에 씌우귀 위해 같은 크기, 같은 위치로 만들어서 document에 추가
    canvas.width = video.width;
    canvas.height = video.height;

    canvas.style.position = "absolute";
    canvas.style.left = video.offsetLeft + "px";
    canvas.style.top = video.offsetTop + "px";

    video.parentNode.appendChild(canvas);

    posenet.load().then((model) => {
        console.log("모델 로드");
        predict();

        function predict() {
            model.estimateSinglePose(video).then((pose) => {
                canvas.width = video.width; //캔버스와 비디오의 크기를 일치시킴
                canvas.height = video.height;
                drawKeypoints(pose.keypoints, 0.6, context); //정확도
                drawSkeleton(pose.keypoints, 0.6, context);
            });
            requestAnimationFrame(predict); //frame이 들어올 때마다 재귀호출
        }
    });
}


//tensorflow에서 제공하는 js 파트
const color = "aqua";
const boundingBoxColor = "red";
const lineWidth = 2;

function toTuple({y, x}) {
    return [y, x];
}

function drawPoint(ctx, y, x, r, color) {
    ctx.beginPath();
    ctx.arc(x, y, r, 0, 2 * Math.PI);
    ctx.fillStyle = color;
    ctx.fill();
}

function drawSegment([ay, ax], [by, bx], color, scale, ctx) {
    ctx.beginPath();
    ctx.moveTo(ax * scale, ay * scale);
    ctx.lineTo(bx * scale, by * scale);
    ctx.lineWidth = lineWidth;
    ctx.strokeStyle = color;
    ctx.stroke();
}

function drawSkeleton(keypoints, minConfidence, ctx, scale = 1) {
    const adjacentKeyPoints = posenet.getAdjacentKeyPoints(keypoints, minConfidence);

    adjacentKeyPoints.forEach((keypoints) => {
        drawSegment(toTuple(keypoints[0].position), toTuple(keypoints[1].position), color, scale, ctx);
    });
}

function drawKeypoints(keypoints, minConfidence, ctx, scale = 1) {
    for (let i = 0; i < keypoints.length; i++) {
        const keypoint = keypoints[i];

        if (keypoint.score < minConfidence) {
            continue;
        }

        const {y, x} = keypoint.position;
        drawPoint(ctx, y * scale, x * scale, 3, color);
    }
}

function drawBoundingBox(keypoints, ctx) {
    const boundingBox = posenet.getBoundingBox(keypoints);

    ctx.rect(
        boundingBox.minX,
        boundingBox.minY,
        boundingBox.maxX - boundingBox.minX,
        boundingBox.maxY - boundingBox.minY
    );

    ctx.strokeStyle = boundingBoxColor;
    ctx.stroke();
}