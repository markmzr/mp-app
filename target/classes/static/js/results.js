setCategory();
setCondition();
setDistance();
setSort();

$('#distanceForm').submit(function() {
    updateDistance();
    return false;
    });

function setCategory() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var category = parameters.get("category");
    var dropdown = document.getElementById("category");

    for (var i = 0; i < dropdown.options.length; i++) {
        if (dropdown.options[i].value == category) {
            dropdown.options[i].selected = true;
        }
    }
}

function setCondition() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var condition = parameters.get("condition");

    switch (condition) {
        case "all":
            document.getElementById("new").checked = false;
            document.getElementById("used").checked = false;
            break;
        case "any":
            document.getElementById("new").checked = true;
            document.getElementById("used").checked = true;
            break;
        case "new":
            document.getElementById("new").checked = true;
            break;
        case "used":
            document.getElementById("used").checked = true;
            break;
    }
}

function setDistance() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);

    if (parameters.has("miles") && parameters.get("miles").length > 0
        && parameters.has("zipCode") && parameters.get("zipCode").length > 0) {
        var xhttp = new XMLHttpRequest();
        var zipCode = parameters.get("zipCode");
        var url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode + ",US&sensor=false&key=" + apiKey;

        xhttp.onload = function() {
            if (this.readyState == 4 && this.status == 200) {
                var jsonResponse = JSON.parse(this.responseText);
                var results = jsonResponse.results;
                var userLocation = results[0].geometry.location;
                var userLatLng = new google.maps.LatLng(userLocation.lat, userLocation.lng);

                var items = document.getElementsByClassName("item");
                var removedItems = [];
                var removeCount = 0;
                for (i = 0; i < items.length; i++) {
                    var itemLatClass = items[i].getElementsByClassName("item-latitude");
                    var itemLatitude = itemLatClass[0].innerHTML;
                    var itemLongClass = items[i].getElementsByClassName("item-longitude");
                    var itemLongitude = itemLongClass[0].innerHTML;

                    var itemLatLng = new google.maps.LatLng(itemLatitude, itemLongitude);
                    var metersDistance = google.maps.geometry.spherical.computeDistanceBetween(userLatLng, itemLatLng);
                    var milesDistance = metersDistance * 0.000621371;

                    var userMiles = parameters.get("miles");
                    if (milesDistance > userMiles) {
                        items[i].id = "id" + removeCount;
                        removeCount++;
                    }
                }
                for (j = 0; j < removeCount; j++) {
                    var removedItem = document.getElementById("id" + j);
                    removedItem.parentNode.removeChild(removedItem);
                }

                var itemCount = document.getElementById("item-count");
                itemCount.innerHTML -= removeCount;

                var resultsMes = document.getElementById("results");
                if (itemCount.innerHTML == 1) {
                    resultsMes.innerHTML = " result for ";
                } else {
                    resultsMes.innerHTML = " results for ";
                }

                var message = document.getElementById("message");
                var keywords = parameters.get("keywords");
                if (keywords.length == 0) {
                    var category = parameters.get("category");
                    if (category == "all") {
                        message.innerHTML = "All Categories";
                    } else {
                        var categoryCapitalized = category.charAt(0).toUpperCase() + category.slice(1);
                        message.innerHTML = categoryCapitalized;
                    }
                } else {
                    message.innerHTML = '"' + keywords + '"';
                }

                var milesInput = document.getElementById("miles");
                milesInput.value = parameters.get("miles");

                var zipCodeInput = document.getElementById("zipCode");
                zipCodeInput.value = parameters.get("zipCode");
            }
        };
        xhttp.open('GET', url, true);
        xhttp.send();
    }
}

function setSort() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var sort = parameters.get("sort");

    if (sort == "relevance") {
        document.getElementById("sort").selectedIndex = 0;
    }
    else if (sort == "priceasc") {
        document.getElementById("sort").selectedIndex = 1;
    }
    else if (sort == "pricedesc") {
        document.getElementById("sort").selectedIndex = 2;
    }
}

function updateCondition() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var newChecked = document.getElementById("new").checked;
    var usedChecked = document.getElementById("used").checked;

    if (newChecked && usedChecked) {
        parameters.set('condition', 'any');
    }
    else if (!newChecked && !usedChecked) {
        parameters.set("condition", "all");
    }
    else if (newChecked) {
        parameters.set('condition', 'new');
    }
    else if (usedChecked) {
        parameters.set('condition', 'used');
    }
    window.history.replaceState({}, '', '/results?' + parameters);
    window.location.assign(window.location.href);
}

function updateDistance() {
    var url = new URL(window.location.href);
    var parameters = new URLSearchParams(url.search);
    var miles = document.getElementById("miles").value;
    var zipCode = document.getElementById("zipCode").value;

    if (!parameters.has("miles") && !parameters.has("zipCode")) {
        parameters.append("miles", miles);
        parameters.append("zipCode", zipCode);
    }
    else {
        parameters.set("miles", miles);
        parameters.set("zipCode", zipCode);
    }
    window.history.replaceState({}, '', '/results?' + parameters);
    window.location.assign(window.location.href);
}

function updateSort(value) {
    var url = new URL(window.location.href);
    var params = new URLSearchParams(url.search);
    params.set("sort", value);
    window.history.replaceState({}, '', '/results?' + params);
    window.location.assign(window.location.href);
}
