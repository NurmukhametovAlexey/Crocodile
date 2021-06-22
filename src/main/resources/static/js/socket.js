let stompClient;
let playerRole;


function connectToSocket() {
    console.log("establishing socket connection with game: " + gameUUID);
    let socket = new SockJS("/game-socket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameUUID, function (response) {

            console.log(response);
            processWsMessage(response);
        });
        }
    );
}

function processWsMessage(response) {
    let data = JSON.parse(response.body);

    if (data.type == "chat") {

        writeMessage(data);

    } else if (data.type == "canvas") {

        //console.log("Got canvas message!")
        drawLine(data.x_start, data.y_start, data.x_finish, data.y_finish);

    }

}