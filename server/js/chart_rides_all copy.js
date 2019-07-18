var chart = c3.generate({
    bindto: "#chart_rides_all",
    data : {
        x : 'x',
        columns : [
            ['x', '2010-01-01', '2011-01-01', '2012-01-01', '2013-01-01', '2014-01-01', '2015-01-01'],
            ['sample', 60, 200, 100, 400, 150, 250]
        ]
    },
    axis : {
        x : {
            type: 'timeseries'
        },
        y : {
            tick : {
                format : function (y) {
                    return d3.time.format("%H:%M:%S")(new Date(new Date('01-01-2016 00:00:00').getTime() + (y * 1000)));
                }
            }
        }
    }
});