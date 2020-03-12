function noOtpAuth() {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timerProgressBar: true,
        onOpen: (toast) => {
            toast.addEventListener("mouseenter", Swal.stopTimer);
            toast.addEventListener("mouseleave", Swal.resumeTimer);
        }
    });
    Toast.fire({
        type: "error",
        title: "OTP Not Valid!"
    });
}

function beautifyJsonString(jsonString) {
    try {
        return JSON.stringify(JSON.parse(jsonString), null, 4);
    } catch (e) {}
    return jsonString;
}

function saveConfig() {
    if (document.getElementById("config").value === "" || document.getElementById("otp").value === "") {
        swal.fire({
            type: "error",
            title: "Oops...",
            text: "Please enter a value in all fields!"
        });
        return;
    }
    const xmlHttpSettingsSend = new XMLHttpRequest();
    xmlHttpSettingsSend.open( "POST", "../api/v1/admin/saveConfig/" + document.getElementById("otp").value, true );
    document.getElementById("otp").value = "";
    xmlHttpSettingsSend.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpSettingsSend.onreadystatechange = function() {
        if (xmlHttpSettingsSend.readyState === 4 && xmlHttpSettingsSend.status === 200) {
            if (JSON.parse(xmlHttpSettingsSend.responseText).status === "ok") {
                const Toast = Swal.mixin({
                    toast: true,
                    position: "top-end",
                    showConfirmButton: false,
                    timer: 3000,
                    onOpen: (toast) => {
                        toast.addEventListener("mouseenter", Swal.stopTimer);
                        toast.addEventListener("mouseleave", Swal.resumeTimer);
                    }
                });
                Toast.fire({
                    type: "success",
                    title: "Saved!"
                });
                return;
            } else if (JSON.parse(xmlHttpSettingsSend.responseText).status === "error") {
                if (JSON.parse(xmlHttpSettingsSend.responseText).error === "no otp auth") {
                    noOtpAuth();
                    return;
                }
            }
            window.location.replace("logout.html");
        }
    };
    xmlHttpSettingsSend.send( "config=" + encodeURIComponent(document.getElementById("config").value) );
}

function loadConfig() {
    if (document.getElementById("otp").value === "") {
        swal.fire({
            type: "error",
            title: "Oops...",
            text: "Please enter the Google Authenticator code!"
        });
        return;
    }
    const xmlHttpSettingsSend = new XMLHttpRequest();
    xmlHttpSettingsSend.open( "POST", "../api/v1/admin/getConfig/" + document.getElementById("otp").value, true );
    document.getElementById("otp").value = "";
    xmlHttpSettingsSend.onreadystatechange = function() {
        if (xmlHttpSettingsSend.readyState === 4 && xmlHttpSettingsSend.status === 200) {
            if (JSON.parse(xmlHttpSettingsSend.responseText).status === "ok") {
                document.getElementById("config").value = beautifyJsonString(decodeURIComponent(JSON.parse(xmlHttpSettingsSend.responseText).config));
                autosize(document.getElementById("config"));
                const Toast = Swal.mixin({
                    toast: true,
                    position: "top-end",
                    showConfirmButton: false,
                    timer: 3000,
                    onOpen: (toast) => {
                        toast.addEventListener("mouseenter", Swal.stopTimer);
                        toast.addEventListener("mouseleave", Swal.resumeTimer);
                    }
                });
                Toast.fire({
                    type: "success",
                    title: "Loaded!"
                });
                return;
            } else if (JSON.parse(xmlHttpSettingsSend.responseText).status === "error") {
                if (JSON.parse(xmlHttpSettingsSend.responseText).error === "no otp auth") {
                    noOtpAuth();
                    return;
                }
            }
            window.location.replace("logout.html");
        }
    };
    xmlHttpSettingsSend.send( null);
}

function sendSettings() {
    if (document.getElementById("w2gDefaultPlayback").value === "") {
        swal.fire({
            type: "error",
            title: "Oops...",
            text: "Please enter a value in all fields!"
        });
        return;
    }
    const xmlHttpSettingsSend = new XMLHttpRequest();
    xmlHttpSettingsSend.open( "POST", "../api/v1/admin/saveSettings", true );
    xmlHttpSettingsSend.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpSettingsSend.onreadystatechange = function() {
        if (xmlHttpSettingsSend.readyState === 4 && xmlHttpSettingsSend.status === 200) {
            if (JSON.parse(xmlHttpSettingsSend.responseText).status === "ok") {
                const Toast = Swal.mixin({
                    toast: true,
                    position: "top-end",
                    showConfirmButton: false,
                    timer: 3000,
                    onOpen: (toast) => {
                        toast.addEventListener("mouseenter", Swal.stopTimer);
                        toast.addEventListener("mouseleave", Swal.resumeTimer);
                    }
                });
                Toast.fire({
                    type: "success",
                    title: "Saved!"
                });
            } else {
                window.location.replace("logout.html");
            }
        }
    };
    xmlHttpSettingsSend.send( "w2gDefaultPlayback=" + encodeURIComponent(document.getElementById("w2gDefaultPlayback").value) );
}

const xmlHttpSettings = new XMLHttpRequest();
xmlHttpSettings.open( "GET", "../api/v1/admin/getSettings", true );
xmlHttpSettings.onreadystatechange = function() {
    if (xmlHttpSettings.readyState === 4 && xmlHttpSettings.status === 200) {
        if (JSON.parse(xmlHttpSettings.responseText).status === "ok") {
            document.getElementById("w2gDefaultPlayback").value = JSON.parse(xmlHttpSettings.responseText).settings.w2gDefaultPlayback;
        } else {
            window.location.replace("logout.html");
        }
    }
};
xmlHttpSettings.send( "w2gDefaultPlayback=" + encodeURIComponent(document.getElementById("w2gDefaultPlayback").value) );
