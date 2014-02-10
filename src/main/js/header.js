$(document).ready(function() {
  var lineData = [{"x": 5, "y":  1},
                  {"x": 2, "y":  0},
                  {"x": 1, "y":  2},
                  {"x": 3, "y":  5},
                  {"x": 4, "y":  8},
                  {"x": 3, "y": 10},
                  {"x": 0, "y":  9}];

  var scale = 10;
  var offset = 2;

  var line_a = d3.svg.line()
    .x(function(d) { return d.x * scale + offset; })
    .y(function(d) { return d.y * scale + offset; })
    .interpolate("basis"); // bundle

  // var line_b = d3.svg.line()
  //   .x(function(d) { return d.x * scale + 12; })
  //   .y(function(d) { return d.y * scale + 8; })
  //   .interpolate("basis"); // bundle

  var line_c = d3.svg.line()
    .x(function(d) { return d.x * scale + offset + 58; })
    .y(function(d) { return d.y * scale + offset; })
    .interpolate("basis"); // bundle
   
  var svg = d3.select("#curve").append("svg")
    .attr("width", 200)
    .attr("height", 200);

  svg.append("rect")
  .attr("width", "56")
  .attr("height", "104")
  .attr("fill", "black");
   
  svg.append("path")
    .attr("d", line_a(lineData))
    .attr("stroke", "white")
    .attr("stroke-width", 6)
    .attr("fill", "none");

  svg.append("rect")
  .attr("x", "58")
  .attr("width", "56")
  .attr("height", "104")
  .attr("fill", "black");
   
  svg.append("path")
    .attr("d", line_c(lineData))
    .attr("stroke", "white")
    .attr("stroke-width", 6)
    .attr("fill", "none");
});
