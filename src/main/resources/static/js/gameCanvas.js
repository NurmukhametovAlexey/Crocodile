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
    canvas.context = canvas.source[0].getContext("2d");
    canvas_context = canvas.context;
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
            let line = {x1:canvas.lastPoint.x, y1:canvas.lastPoint.y, x2: e.offsetX, y2: e.offsetY};
            drawLine(line);

            /*if (conn.readyState === 1) {
                conn.send(JSON.stringify({
                    sender:sender,
                    type:"canvas",
                    part:canvas.part,
                    line:line
                }));
            }*/

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

function drawLine(line) {

    canvas_context.beginPath();
    canvas_context.moveTo(line.x1, line.y1);
    canvas_context.lineTo(line.x2, line.y2);

    canvas_context.closePath();
    canvas_context.stroke();
};