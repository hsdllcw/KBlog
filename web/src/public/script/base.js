window.ajaxServerURL = () => {
    switch (document.domain) {
        default: return "http://localhost/api";
    }
};

window.backAjaxServerURL = function () {
    return `${window.ajaxServerURL()}/admin`
};

window.NODE_ENV = process.env.NODE_ENV;

window.applicationName=()=>{
    switch (document.domain) {
        default: return "localhost";
    }
}
