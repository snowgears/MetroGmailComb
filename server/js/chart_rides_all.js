var chart = c3.generate({
    bindto: "#chart_rides_all",
    data : {
        x : 'x',
        xFormat: '%m-%d-%Y',
        url: '/json/metro_rides_all.json',
        mimeType: 'json'
    },
    axis : {
        x : {
            type: 'timeseries',
            tick: {
                 format: '%m-%d-%Y'
             }
        },
        y : {
            tick : {
                format : function (y) {
                    var yDate = new Date();
                    var yHour = parseInt(y / 60);
                    var yMinute = y % 60;
                    yDate.setHours(yHour);
                    yDate.setMinutes(yMinute);
                    return d3.time.format("%I:%M %p")(yDate);
                }
            }
        }
    }
});