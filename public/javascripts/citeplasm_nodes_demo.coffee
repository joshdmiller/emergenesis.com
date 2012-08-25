data = 
    vertices: [
        { # 00
            label: "Antoine-Laurent de Lavoisier"
        },
        { # 01
            label: "Pierre-Simon Laplace"
        },
        { # 02
            label: "Jacques Monod"
        },
        { # 03
            label: "Pierre Curie"
        },
        { # 04
            label: "Chemist"
        },
        { # 05
            label: "Physicist"
        },
        { # 06
            label: "Mathematician"
        },
        { # 07
            label: "Biologist"
        },
        { # 08
            label: "Roman Catholicism"
        },
        { # 09
            label: "Atheism"
        },
        { # 10
            label: "Place de la Concorde"
        },
        { # 11
            label: "Paris"
        },
        { # 12
            label: "Decapitation"
        },
        { # 13
            label: "France"
        }
    ],

    edges: [
        { # lavoisier --profession--> chemist
            source: 0, target: 4, label: "profession"
        },
        { # laplace --profession--> mathematician
            source: 1, target: 6, label: "profession"
        },
        { # curie --profession--> physicist
            source: 3, target: 5, label: "profession"
        },
        { # monod --profession--> biologist
            source: 2, target: 7, label: "profession"
        },
        { # lavoisier --religion--> roman cath
            source: 0, target: 8, label: "religion"
        },
        { # laplace --religion--> roman cath
            source: 1, target: 8, label: "religion"
        },
        { # monod --religion--> atheism
            source: 2, target: 9, label: "religion"
        },
        { # lav --place_of_birth--> paris
            source: 0, target: 11, label: "place_of_birth"
        },
        { # lav --place_of_death--> place de la concord
            source: 0, target: 10, label: "place_of_death"
        },
        { # lav --cause_of_death--> decap
            source: 0, target: 12, label: "cause_of_death"
        },
        { # lav --similar_to--> monod
            source: 0, target: 2, label: "similar_to"
        },
        { # lav <--similar_to-- monod
            source: 2, target: 0, label: "similar_to"
        },
        { # lav --similar_to--> curie
            source: 0, target: 3, label: "similar_to"
        },
        { # lav <--similar_to-- curie
            source: 3, target: 0, label: "similar_to"
        },
        { # lav --similar_to--> laplace
            source: 0, target: 1, label: "similar_to"
        },
        { # lav <--similar_to-- laplace
            source: 1, target: 0, label: "similar_to"
        },
        { # paris --in--> france
            source: 11, target: 13, label: "located_in"
        },
        { # concorde --in--> france
            source: 10, target: 11, label: "located_in"
        }
    ]

graph_width         = 700
graph_height        = 400
vertex_radius       = 10

vis = graph = edges = vertices = info = null
edgeIndex = []

graph_tick = (v) ->
    data.vertices[0].x = graph_width / 2
    data.vertices[0].y = graph_height / 2

    edges.attr "x1", (e) -> e.source.x
    edges.attr "x2", (e) -> e.target.x
    edges.attr "y1", (e) -> e.source.y
    edges.attr "y2", (e) -> e.target.y

    #vertices.attr "cx", (v) -> v.x
    #vertices.attr "cy", (v) -> v.y

    vertices.attr "transform", (v) -> 
        return "translate(" + v.x + "," + v.y + ")"

is_node_connected = (n1, n2) ->
    return edgeIndex[n1.index + "," + n2.index] || edgeIndex[n2.index + "," + n1.index] || n1.index == n2.index

is_edge_connected = (v, e) ->
    return e.source == v || e.target == v

activate = (d, i) ->
    vis.classed "hover", true
    vertices.classed "active", (v) ->
        return is_node_connected(d, v)
    edges.classed "active", (e) ->
        return is_edge_connected(d, e)

deactivate = () ->
    vis.classed "hover", false
    edges.classed "active", false

show_info = (v, i) ->
    d3.select(".citeplasmChart .modal .modalLabel").text v.label
    $(".citeplasmChart .modal").modal()

start_visualization = () ->
    vis = d3.select(".citeplasmChart").append("svg:svg")
        .attr("width", graph_width)
        .attr("height", graph_height)

    # Create an index of edges so we can know which nodes are directly connected
    # to which other nodes. [source,target] = [target,source] 1
    for e in data.edges
        edgeIndex["#{e.source},#{e.target}"] = 1
        edgeIndex["#{e.target},#{e.source}"] = 1
    
    # Create the force-directed graph.
    graph = d3.layout.force()
        .charge(-500)
        .linkDistance(vertex_radius * 10)
        .gravity(.1)
        .size([graph_width,graph_height])
        .nodes(data.vertices)
        .links(data.edges)
        .on("tick", graph_tick)
        .start()

    # Draw all of the edges from the data.
    edges = vis.selectAll("line.link")
        .data(data.edges)
        .enter().append("svg:line")
        .attr("class", "edge")
        .attr("x1", (e) -> e.source.x)
        .attr("x2", (e) -> e.target.x)
        .attr("y1", (e) -> e.source.y)
        .attr("y2", (e) -> e.target.y)
        .attr("title", (e) -> e.label)

    # Create container nodes for the vertices.
    vertices = vis.selectAll("g.node")
        .data(data.vertices)
        .enter().append("svg:g")
        .attr("class", "vertex")
        .call(graph.drag)

    # Add a circle to the vertices to act as the node.
    vertices.append("svg:circle")
        .attr("class", "vcircle")
        .attr("x", vertex_radius / -2)
        .attr("y", vertex_radius / -2)
        .attr("r", vertex_radius)
        .attr("title", (v) -> v.label)
        
    # Add a text label to the vertices.
    vertices.append("svg:text")
        .attr("class", "vlabel")
        .attr("dx", vertex_radius + 5)
        .attr("dy", 5)
        .text (v) -> return v.label

    # On mouseover of a node's circle, dim all others. Remove on mouseout.
    vertices.selectAll("circle.vcircle")
        .on("mouseover", activate)
        .on("mouseout", deactivate)
        .on("click", show_info)

# Start the visualization when the button is clicked.
$(document).ready () ->
    chart = $(".citeplasmChart")
    $("> p > a.btn", chart).click () ->
        $("> p", chart).remove()
        chart.addClass("active")
        start_visualization()

