$( document ).ready(function() {

    connectToSocket();

    let cnv = document.getElementById("canvas-game");
    let window_height = window.innerHeight - $("#navbar-element").height();
    cnv.height=window_height*0.7;
    cnv.width=window.innerWidth*0.6;
    cnv.style.border="1px solid blue";

    console.log(chat);

    initPainting();

    if(playerRole === "PAINTER") {
        hideChat();
    }
    else if(playerRole === "GUESSER") {
        disableCanvas();
    };


    $("#form-chat").submit(function (event) {
        event.preventDefault();

        //stompClient.disconnect();

        let msg = document.getElementById("form-chat").msg.value;
        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "chat",
                "message": msg
            }));
    });

});

function writeMessage(message){

    /*let now = new Date().toLocaleString()
    document.getElementById("chat").innerHTML = document.getElementById("chat").innerHTML + now  + " | " + message.sender + ": " + message.message + "<br />";
*/
    document.getElementById("chat").innerHTML =
        document.getElementById("chat").innerHTML + message + "<br />";
}

function hideChat() {
    document.getElementById("msg").style.display = "none";
}

