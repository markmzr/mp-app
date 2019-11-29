$('#editItemForm').submit(function(event) {
    getItemLatLong(setItemLatLong);
    event.preventDefault();
    });

function getItemLatLong(setItemLatLong) {
    var xhttp = new XMLHttpRequest();
    var zipCode = document.getElementById("zipCode").value;
    var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode + ",US&sensor=false&key=" + apiKey;

    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var jsonResponse = JSON.parse(this.responseText);
            var results = jsonResponse.results;
            var latitude = results[0].geometry.location.lat;
            var longitude = results[0].geometry.location.lng;
            setItemLatLong(latitude, longitude);
        }
        if (this.readyState == 4 && this.status == 400) {
            setItemLatLong(0, 0);
        }
    };
    xhttp.open("GET", url, true);
    xhttp.send();
}

function setItemLatLong(latitude, longitude) {
    var latitudeDiv = document.createElement("input");
    latitudeDiv.setAttribute("type", "text");
    latitudeDiv.setAttribute("name", "latitude");
    latitudeDiv.setAttribute("value", latitude);
    latitudeDiv.style.display = "none";
    document.getElementById("editItemForm").appendChild(latitudeDiv);

    var longitudeDiv = document.createElement("input");
    longitudeDiv.setAttribute("type", "text");
    longitudeDiv.setAttribute("name", "longitude");
    longitudeDiv.setAttribute("value", longitude);
    longitudeDiv.style.display = "none";
    document.getElementById("editItemForm").appendChild(longitudeDiv);

    document.getElementById("editItemForm").submit();
}
