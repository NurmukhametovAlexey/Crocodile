$( document ).ready(function() {

    let cnv = document.getElementById("canvas-game");
    let window_height = window.innerHeight - $("#navbar-element").height();
    cnv.height=window_height*0.7;
    cnv.width=window.innerWidth*0.6;
    cnv.style.border="1px solid blue";

    alert("gameCanvas Loaded! " + cnv.height.toString());

    initPainting();
});

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

    canvas.source.bind("mousedown", function(e) {
        canvas.isPainting = true;
        canvas.lastPoint = {x: e.offsetX, y: e.offsetY};
    });

    canvas.source.bind("mousemove", function(e) {
        if (canvas.isPainting) {

            stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify({
                "type": "canvas",
                "x_start": canvas.lastPoint.x,
                "y_start": canvas.lastPoint.y,
                "x_finish": e.offsetX,
                "y_finish": e.offsetY
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

function drawLine(xStart, yStart, xFinish, yFinish) {
    canvas_context.beginPath();

    canvas_context.moveTo(xStart, yStart);
    canvas_context.lineTo(xFinish, yFinish);

    canvas_context.closePath();
    canvas_context.stroke();
};