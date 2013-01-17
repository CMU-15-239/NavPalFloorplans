function [path, shortestPath] = f_DijkstrasAlgorithm(graphEdgeList, n, s, t, graphType)

%
% This function is an implementaiton of Dijkstra's algorithm translated
% from the book Discrete Optimization Algorithms with Pascal Programs by
% Syslo, Deo and Kowalik (pp. 233-235). It will find the shortest path
% between a source node s and a destination node t given a graph G with
% vertices V and edges E.
%
% Parameters:
%
%       graphEdgeList - The list of edges connecting the vertices of the
%                       graph. TODO Finish this description.
%
%       graphVertices - The vertices of the graph. Each vertice has the
%                       following structure. TODO Finish this description.
%
%       s - The index of the starting node.
%
%       t - The index of the ending node.
%
%

    %
    % Initializations
    %
    w = ones(n, n) * Inf;           % Weight matrix initialized to infinity
    dist = ones(1, n) * Inf;        % Will contain the shortest distance from S to T
    final = zeros(1, n);            % Boolean vector initialized to false. When node is permanantly labeled, its corresponding element in this vector is set to true (or 1)
    pred = -ones(1, n);

    % Output variables
    dist(s) = 0;
    final(s) = 1;
    path = 1;                       % Boolean, 1 if path between s and t was found, false otherwise
    recent = s;

    % Create the cost/weight matrix w
    numberOfEdges = size(graphEdgeList, 1);
    for i=1:numberOfEdges

        
        % Check if edge is valid
        if (graphEdgeList(i ,4) == 1)

            v1 = graphEdgeList(i, 1);
            v2 = graphEdgeList(i, 2);
            cost = graphEdgeList(i, 3);

            w(v1, v2) = cost;

            if (strcmp(graphType, 'undirected'))
                w(v2, v1) = cost;
            end
        end
    end

    %
    % Dijkstra's Algorithm
    %
    while (~final(t))
        for v=1:n
            if  (w(recent, v) < Inf) && (~final(v))
                newlabel = dist(recent) + w(recent, v);
                if newlabel < dist(v)
                    dist(v) = newlabel;
                    pred(v) = recent;
                end
            end
        end

        temp = Inf;
        y = 1;

        for u=1:n
            if (~final(u)) && (dist(u) < temp)
                y = u;
                temp = dist(u);
            end
        end

        if (temp < Inf)

            final(y) = 1;
            recent = y;

        else
            path = 0;
            final(t) = 1;
        end
    end

    %
    % Construct the shortest path, if it exists
    %
    shortestPath = zeros(1, n);
    if (path)

        currentNode = t;
        insertionIndex = 1;
        while (currentNode ~= s);
        
            shortestPath(insertionIndex) = currentNode;
            insertionIndex = insertionIndex + 1;
            currentNode = pred(currentNode);

        end
        shortestPath(insertionIndex) = s;

        % Remove the zeros from the shortest path (if any) and reverse the path
        shortestPath = shortestPath(shortestPath ~= 0);
        shortestPath = shortestPath(end:-1:1);
    end

end
