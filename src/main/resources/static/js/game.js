$( document ).ready(function() {

    connectToSocket();
       //alert("game loaded");
    $("#btn-chat-message").click(function () {
        //alert("btn-chat-message");
        writeMessage();


/*        stompClient.send("/app/test", {}, JSON.stringify(
            {"message": "Hope this works!",
                "gameUUID": gameUUID,
                "userLogin": "test login"
            }));*/
    });
});

function writeMessage(){
    let form = document.getElementById("form-chat");
    let now = new Date().toLocaleString()
    let user = form.username.value;
    let message = form.msg.value;
    document.getElementById("chat").innerHTML = document.getElementById("chat").innerHTML + now  + " | " + user + " said: " + message + "<br />";

    $.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "message": message,
            "gameUUID": gameUUID,
            "userLogin": currentUser
        }),
        success: function (data) {
            alert("message sent to controller. Answer: " + data)
        },
        error: function (error) {
            console.log(error);
        }
    })

}