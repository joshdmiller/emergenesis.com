// Generated by CoffeeScript 1.3.3
var activate, data, deactivate, edgeIndex, edges, graph, graph_height, graph_tick, graph_width, info, is_edge_connected, is_node_connected, max_weight, min_weight, show_info, start_visualization, vertex_max_radius, vertex_min_radius, vertices, vis;

data = {
  vertices: [
    {
      label: "Antoine-Laurent de Lavoisier"
    }, {
      label: "Pierre-Simon Laplace"
    }, {
      label: "Jacques Monod"
    }, {
      label: "Pierre Curie"
    }, {
      label: "Chemist"
    }, {
      label: "Physicist"
    }, {
      label: "Mathematician"
    }, {
      label: "Biologist"
    }, {
      label: "Roman Catholicism"
    }, {
      label: "Atheism"
    }, {
      label: "Place de la Concorde"
    }, {
      label: "Paris"
    }, {
      label: "Decapitation"
    }, {
      label: "France"
    }
  ],
  edges: [
    {
      source: 0,
      target: 4,
      label: "profession"
    }, {
      source: 1,
      target: 6,
      label: "profession"
    }, {
      source: 3,
      target: 5,
      label: "profession"
    }, {
      source: 2,
      target: 7,
      label: "profession"
    }, {
      source: 0,
      target: 8,
      label: "religion"
    }, {
      source: 1,
      target: 8,
      label: "religion"
    }, {
      source: 2,
      target: 9,
      label: "religion"
    }, {
      source: 0,
      target: 11,
      label: "place_of_birth"
    }, {
      source: 0,
      target: 10,
      label: "place_of_death"
    }, {
      source: 0,
      target: 12,
      label: "cause_of_death"
    }, {
      source: 0,
      target: 2,
      label: "similar_to"
    }, {
      source: 2,
      target: 0,
      label: "similar_to"
    }, {
      source: 0,
      target: 3,
      label: "similar_to"
    }, {
      source: 3,
      target: 0,
      label: "similar_to"
    }, {
      source: 0,
      target: 1,
      label: "similar_to"
    }, {
      source: 1,
      target: 0,
      label: "similar_to"
    }, {
      source: 11,
      target: 13,
      label: "located_in"
    }, {
      source: 10,
      target: 11,
      label: "located_in"
    }
  ]
};

graph_width = 700;

graph_height = 400;

vertex_max_radius = 20;

vertex_min_radius = 5;

max_weight = min_weight = 1;

vis = graph = edges = vertices = info = null;

edgeIndex = [];

graph_tick = function(v) {
  data.vertices[0].x = graph_width / 2;
  data.vertices[0].y = graph_height / 2;
  vertices.attr("transform", function(v) {
    var r, x, y;
    r = parseInt(d3.select(this.parentNode).select("circle").attr("r"));
    x = Math.max(r, Math.min(graph_width - r, v.x));
    y = Math.max(r, Math.min(graph_height - r, v.y));
    v.x = x;
    v.y = y;
    return "translate(" + x + "," + y + ")";
  });
  edges.attr("x1", function(e) {
    return e.source.x;
  });
  edges.attr("x2", function(e) {
    return e.target.x;
  });
  edges.attr("y1", function(e) {
    return e.source.y;
  });
  return edges.attr("y2", function(e) {
    return e.target.y;
  });
};

is_node_connected = function(n1, n2) {
  return edgeIndex[n1.index + "," + n2.index] || edgeIndex[n2.index + "," + n1.index] || n1.index === n2.index;
};

is_edge_connected = function(v, e) {
  return e.source === v || e.target === v;
};

activate = function(d, i) {
  vis.classed("hover", true);
  vertices.classed("active", function(v) {
    return is_node_connected(d, v);
  });
  return edges.classed("active", function(e) {
    return is_edge_connected(d, e);
  });
};

deactivate = function() {
  vis.classed("hover", false);
  return edges.classed("active", false);
};

show_info = function(v, i) {
  d3.select(".citeplasmChart .modal .modalLabel").text(v.label);
  return $(".citeplasmChart .modal").modal();
};

start_visualization = function() {
  var e, v, _i, _j, _len, _len1, _ref, _ref1;
  vis = d3.select(".citeplasmChart").append("svg:svg").attr("width", graph_width).attr("height", graph_height);
  _ref = data.edges;
  for (_i = 0, _len = _ref.length; _i < _len; _i++) {
    e = _ref[_i];
    edgeIndex["" + e.source + "," + e.target] = 1;
    edgeIndex["" + e.target + "," + e.source] = 1;
  }
  graph = d3.layout.force().charge(-500).linkDistance(vertex_max_radius * 5).gravity(.1).size([graph_width, graph_height]).nodes(data.vertices).links(data.edges).on("tick", graph_tick).start();
  _ref1 = data.vertices;
  for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
    v = _ref1[_j];
    if (v.weight > max_weight) {
      max_weight = v.weight;
    }
  }
  edges = vis.selectAll("line.link").data(data.edges).enter().append("svg:line").attr("class", "edge").attr("x1", function(e) {
    return e.source.x;
  }).attr("x2", function(e) {
    return e.target.x;
  }).attr("y1", function(e) {
    return e.source.y;
  }).attr("y2", function(e) {
    return e.target.y;
  }).attr("title", function(e) {
    return e.label;
  });
  vertices = vis.selectAll("g.node").data(data.vertices).enter().append("svg:g").attr("class", "vertex").call(graph.drag);
  vertices.append("svg:circle").attr("class", "vcircle").attr("r", function(v) {
    return v.weight / max_weight * (vertex_max_radius - vertex_min_radius) + vertex_min_radius;
  }).attr("title", function(v) {
    return v.label;
  });
  vertices.append("svg:text").attr("class", "vlabel").attr("dx", function(v, i) {
    var n;
    n = parseFloat(d3.select(this.parentNode).select("circle").attr("r"));
    return n + 5.0;
  }).attr("dy", 5).text(function(v) {
    return v.label;
  });
  return vertices.selectAll("circle.vcircle").on("mouseover", activate).on("mouseout", deactivate).on("click", show_info);
};

$(document).ready(function() {
  var chart;
  chart = $(".citeplasmChart");
  graph_width = chart.parent().width() - 2;
  return $("> p > a.btn", chart).click(function() {
    $("> p", chart).remove();
    chart.addClass("active");
    return start_visualization();
  });
});