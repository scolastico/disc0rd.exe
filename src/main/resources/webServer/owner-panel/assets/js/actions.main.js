function sendMessage() {
    if (document.getElementById("title").value === "" || document.getElementById("message").value === "") {
        swal.fire({
           icon: "error",
           title: "Oops...",
           text: "Please enter a value in all fields!"
        });
        return;
    }
    const xmlHttp_actions_send = new XMLHttpRequest();
    xmlHttp_actions_send.open( "POST", "../api/v1/admin/sendMessage", true );
    xmlHttp_actions_send.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttp_actions_send.timeout = 60000;
    xmlHttp_actions_send.onreadystatechange = function() {
        if (xmlHttp_actions_send.readyState === 4 && xmlHttp_actions_send.status === 200) {
            if (JSON.parse(xmlHttp_actions_send.responseText).status === "ok") {
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true,
                    onOpen: (toast) => {
                        toast.addEventListener('mouseenter', Swal.stopTimer)
                        toast.addEventListener('mouseleave', Swal.resumeTimer)
                    }
                });
                Toast.fire({
                    icon: 'success',
                    title: 'Message Send!'
                });
            } else {
                window.location.replace("logout.html");
            }
        }
    };
    xmlHttp_actions_send.send( "title=" + encodeURI(document.getElementById("title").value) + "&message=" + encodeURI(document.getElementById("message").value) );
    document.getElementById("title").value = "";
    document.getElementById("message").value = "";
}