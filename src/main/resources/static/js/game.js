

$( document ).ready(function() {

       //alert("game loaded");
    $("#btn-chat-message").click(function () {
        //alert("btn-chat-message");
        writeMessage();
    });

});

function writeMessage(){
    let form = document.getElementById("form-chat");
    let now = new Date().toLocaleString()
    let user = form.username.value;
    let message = form.msg.value;
    document.getElementById("chat").innerHTML = document.getElementById("chat").innerHTML + now  + " | " + user + " said: " + message + "<br />";
}