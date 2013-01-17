function [graphEdges, graphVertices] = f_CreateFullyConnectedGraph(graphVertices)
%
% Parameters:
% 
%   vertices - A list of the nodes in the graph. The list is an N x 4
%              vector with the following format
%
%                   [unique id, x-coordinate, y-coordinate, z-coordinate]
%
%               Note that current uses of the function will be limited to
%               the 2D plane and the z component will remain 0. However, if
%               3D space will be used, the z-component can be defined. 
%
% Returns
%
%   grapgEdges - An N x 3 matrix of edges is returned. The format for each
%                edge is [source node, destination node, cost, valid]
%

    % Allocate space for the edges. Note that a graph with N vertices
    % produces N*(N-1)/2 edges for a fully connected graph with no self
    % referential edges.
    numberOfVertices = size(graphVertices, 1);
    graphEdges = ones((numberOfVertices*(numberOfVertices-1)/2), 4);

    % Compute all edges for the graph
    index = 1;
    for i=1:numberOfVertices-1

        for j=(i+1):numberOfVertices

            % Compute the cost between the ith and jth vertice
            edgeCost = f_EuclideanDistance(graphVertices(i, 2:4), graphVertices(j, 2:4));

            % Add the edge to the list of edges
            graphEdges(index, 1:3) = [graphVertices(i, 1), graphVertices(j, 1), edgeCost];
            index = index + 1;

        end
       
    end
end
