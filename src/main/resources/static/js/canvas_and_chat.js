let canvas = {};
let canvas_context;

function initPainting() {
    canvas.source = $("#canvas-game");
    canvas_context = canvas.source[0].getContext("2d");
    initCanvas();
}

function initCanvas() {
    canvas.isPainting = false;
    canvas.lastPoint = {};

    canvas_context.shadowColor =  "rgba(0, 0, 0, 0.75)";
    canvas_context.shadowBlur = 5;
    canvas_context.shadowOffsetX = 0;
    canvas_context.shadowOffsetY = 0;

    if (playerRole === "PAINTER") {
        canvas.source.bind("mousedown", function(e) {
            canvas.isPainting = true;
            canvas.lastPoint = {x: e.offsetX, y: e.offsetY};
        });

        canvas.source.bind("mousemove", function(e) {
            if (canvas.isPainting) {

                stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify({
                    "type": "canvas",
                    "x_start": canvas.lastPoint.x / window_width,
                    "y_start": canvas.lastPoint.y / window_height,
                    "x_finish": e.offsetX / window_width,
                    "y_finish": e.offsetY / window_height
                }));

                canvas.lastPoint = {x: e.offsetX, y: e.offsetY};
            }
        });

        canvas.source.bind("mouseup", function(e) {
            canvas.isPainting = false;
        });

        canvas.source.bind("mouseleave", function () {
            canvas.isPainting = false;
        })
    }
}

function drawLine(xStart, yStart, xFinish, yFinish) {
    canvas_context.beginPath();

    canvas_context.moveTo(xStart * window_width, yStart * window_height);
    canvas_context.lineTo(xFinish * window_width, yFinish * window_height);

    canvas_context.closePath();
    canvas_context.stroke();
};

function disableCanvas() {
    if (canvas.source) {
        canvas.source.unbind("mousedown");
    }
}

function writeMessage(message){
    document.getElementById("chat").innerHTML =
        document.getElementById("chat").innerHTML + message + "<br />";
}

function hideChat() {
    let chat = document.getElementById("msg");
    if (chat) {
        chat.style.display = "none";
    }
}

function hideStartButton() {
    let startButton = document.getElementById("btn-start-game");
    if (startButton) {
        startButton.style.display = "none";
    }
}

function hideClearCanvasButton() {
    let clearCanvasButton = document.getElementById("btn-canvas-clear");
    if (clearCanvasButton) {
        clearCanvasButton.style.display = "none";
    }
}

function showCanvasClearButton() {
    let canvasClearButton = document.getElementById("btn-canvas-clear");
    if (canvasClearButton) {
        canvasClearButton.style.display = "block";
    }
}

function beginTheGame() {
    initPainting();
    if(playerRole === "PAINTER") {
        hideChat();
        hideStartButton();
        showCanvasClearButton();
        document.getElementById("game-secret-word").innerHTML += "<b>" + secretWord + "</b><br />";
    }
    else if(playerRole === "GUESSER") {
        disableCanvas();
    };
}

function endTheGame() {
    hideChat();
    disableCanvas();
    hideClearCanvasButton();
}