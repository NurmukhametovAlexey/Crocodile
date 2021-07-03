let stompClient;

function connectToSocket() {
    console.log("establishing socket connection with game: " + gameUUID);
    let socket = new SockJS("/game-socket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
            console.log("connected to the frame: " + frame);

            stompClient.subscribe("/topic/game-progress/" + gameUUID, function (response) {

                processWsMessage(response);

            });
        }
    );

}

function processWsMessage(response) {


    let data = JSON.parse(response.body);

    if (data.type == "canvas") {

        drawLine(data.x_start, data.y_start, data.x_finish, data.y_finish);

    } else if (data.type == "chat") {

        console.log(data);
        writeMessage(data.message);
        if(data.victory == true) {
            endTheGame();
        }


    }  else if (data.type === "command") {

        console.log(data);

        if (data.command === "clear canvas") {
            console.log("clear canvas");
            clearCanvas();
        }
        else if (data.command === "begin game") {
            console.log("begin game");
            beginTheGame();
        }
        else if (data.command === "cancel game") {
            console.log("cancel game");
            endTheGame();
            stompClient.disconnect();
        }
        else if (data.command === "upload canvas") {
            console.log("upload canvas command message");
            uploadCanvas();
            /*if(gameStatus === "IN_PROGRESS") {
                uploadCanvas();
            }*/
        }
    };

}