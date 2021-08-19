let canvas = {};
let canvas_context;
const default_resolution = {w: 640, h: 480};

function initPainting() {
    canvas.source = $("#canvas-game");
    canvas_context = canvas.source[0].getContext("2d");
    initCanvas();
    initColorTiles();
}

function initCanvas() {
    canvas.isPainting = false;
    canvas.lastPoint = {};

    if (playerRole === "PAINTER") {
        canvas.source.bind("mousedown", function(e) {
            canvas.isPainting = true;
            canvas.lastPoint = {x: e.offsetX, y: e.offsetY};
        });

        canvas.source.bind("mousemove", function(e) {
            if (canvas.isPainting) {

                stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify({
                    "type": "canvas",
                    "x_start": canvas.lastPoint.x / W,
                    "y_start": canvas.lastPoint.y / H,
                    "x_finish": e.offsetX / W,
                    "y_finish": e.offsetY / H
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

function initColorTiles() {
    let color_tile = document.getElementById("white-color");
    let color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "white";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);

    color_tile = document.getElementById("yellow-color");
    color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "yellow";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);

    color_tile = document.getElementById("yellow-color");
    color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "yellow";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);

    color_tile = document.getElementById("green-color");
    color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "green";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);

    color_tile = document.getElementById("red-color");
    color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "red";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);

    color_tile = document.getElementById("blue-color");
    color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "blue";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);

    color_tile = document.getElementById("black-color");
    color_tile_ctx = color_tile.getContext('2d');
    color_tile_ctx.fillStyle = "black";
    color_tile_ctx.fillRect(0, 0, color_tile.width, color_tile.height);
}


function drawLine(xStart, yStart, xFinish, yFinish) {
    canvas_context.beginPath();

    canvas_context.moveTo(xStart * W, yStart * H);
    canvas_context.lineTo(xFinish * W, yFinish * H);

    canvas_context.closePath();
    canvas_context.stroke();
};

function changeWidth() {
    let line_width = document.getElementById("canvas-width-range").value;
    stompClient.send("/topic/game-progress/" + gameUUID, {}, JSON.stringify(
        {
            "type": "canvas width",
            "width": line_width
        }));
}

function changeColor(color) {
    stompClient.send("/topic/game-progress/" + gameUUID, {}, JSON.stringify(
        {
            "type": "canvas color",
            "color": color
        }));
}

function clearCanvas() {
    canvas_context.clearRect(0, 0, canvas.source[0].width, canvas.source[0].height);
    /*canvas_context.strokeStyle = 'black';
    canvas_context.lineWidth = '2';*/
    canvas_context.strokeRect(0, 0, window.innerWidth * 0.55, window.innerHeight * 0.7);
}

function disableCanvas() {
    if (canvas.source) {
        canvas.source.unbind("mousedown");
    }
}

function uploadCanvas() {
    //let canvas_image = canvas_context.getImageData(0,0, W, H);
    if(canvas.source) {
        let canvas_image = canvas.source[0].toDataURL('image/png');
        console.log("uploading canvas...");
        $.ajax({
            url: "/game/" + gameUUID + "/upload-canvas",
            type: "POST",
            data: canvas_image,
            processData: false,
            contentType: false,
            success: function(response) {
                console.log("canvas successfully uploaded");
                // .. do something
            },
            error: function(jqXHR, textStatus, errorMessage) {
                console.log(errorMessage); // Optional
            }
        });
    }
}

function downloadCanvas() {
    $.ajax({
        url: "/game/" + gameUUID + "/download-canvas",
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        success: function (data) {
            console.log("got canvas image!");
            let image = new Image();
            image.onload = function() {
                ctx.drawImage(image, 0, 0, W, H);
            };
            image.src = "data:image/png;base64," + data.image;
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function writeMessage(message){
    let chatbox = document.getElementById("chat-box");
    chatbox.innerHTML = chatbox.innerHTML + message + "<br />";
    chatbox.scrollTop = chatbox.scrollHeight;
}

function hideElement(id) {
    let el = document.getElementById(id);
    if (el) {
        el.style.display = "none";
    }
}

function showElement(id) {
    let el = document.getElementById(id);
    if (el) {
        el.style.display = "block";
    }
}

function beginTheGame() {
    initPainting();
    if(playerRole === "PAINTER") {
        hideElement("form-chat-input");
        hideElement("btn-start-game");
        showElement("btn-canvas-clear");
        showElement("canvas-width-range");
        showElement("canvas-color-pick");
        document.getElementById("game-secret-word").innerHTML += "<b>" + secretWord + "</b><br />";
    }
    else if(playerRole === "GUESSER") {
        disableCanvas();
    };
}

function endTheGame() {
    uploadCanvas();
    hideElement("form-chat-input");
    hideElement("btn-canvas-clear");
    hideElement("canvas-width-range");
    hideElement("canvas-color-pick");
    hideElement("link-game-cancel");
    hideElement("link-game-leave");
    disableCanvas();
}