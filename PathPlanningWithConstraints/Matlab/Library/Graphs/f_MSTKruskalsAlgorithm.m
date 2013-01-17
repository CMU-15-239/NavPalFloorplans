function [mstNodes, mstEdges, mstWeight] = f_MSTKruskalsAlgorithm(edgeList, graphNodes)

    % Check base case, only one node in the MST
    [row, col] = size(graphNodes);
    if (col == 1)
        mstNodes(1) = graphNodes(1);
        mstEdges    = [];
    else
        % Create set for each node where node is the only element in the set
        sets = arrayfun(@(x) [x], graphNodes, 'un', false);

        % Sort the rows of the edge list based on edge cost (columm 3)
        edgeList = sortrows(edgeList, 3);

        % Create an empty set for MST edges and node
        mstNodes = [];
        mstEdges = [];
        mstNodeIndex = 1;
        mstEdgeIndex = 1;

        % Build the minimum spanning tree
        [edgeListSize, n] = size(edgeList);
        for i=1:edgeListSize

            currentEdge = edgeList(i,:);    % Get the next edge from the edge list

            % Get the nodes from the current edge and check if they already exist
            % in the mst node set.
            u = currentEdge(1);	% Get the src node
            v = currentEdge(2);	% Get the dest node

            % Find the ids of the sets containing u and v
            [uCellIndex, uSetID] = mstfindSet(sets, u);
            [vCellIndex, vSetID] = mstfindSet(sets, v);

            % If mst find set returns a -1, this means node u or v was not in
            % any of the sets. This should not happen and if it does, there is
            % semantic errors within this code.
            if ((uSetID == -1) || (vSetID == -1))
                msg = sprintf('findset returned a -1');
                disp(msg);
                return;
            end

            % If the nodes u and v are not part of the same set...
            if (uSetID ~= vSetID)
                % Add set containing v to set containing u
                sets = mstunion(sets, uCellIndex, vCellIndex);

                % Add the current edge to the set of mst edges
                mstEdges(mstEdgeIndex,:) = currentEdge;
                mstEdgeIndex = mstEdgeIndex + 1;

                mstNodes(mstNodeIndex) = currentEdge(1);            
                mstNodeIndex = mstNodeIndex + 1;
                mstNodes(mstNodeIndex) = currentEdge(2);            
                mstNodeIndex = mstNodeIndex + 1;
            end        
        end

        mstNodes = unique(mstNodes);
    end
    
    % Compute the weight of the MST
    if (mstEdges)    
        mstWeight = sum(mstEdges(:,3));
    else
        mstWeight = 0;
    end

% OLD CODE, NOT NEEDED BUT WILL KEEP IN CASE WE NEED TO DO SOMETHING SIMILAR
%         result1 = find(mstNodes == u);  % Check if the src node already exists in the mst node set
%         [row col] = size(result1);
%         if (col == 0)
% %             msg = sprintf('Node %d does not exist in mst node set...adding', u);
% %             disp(msg);
%             mstNodes(mstNodeIndex) = u;
%             mstNodeIndex = mstNodeIndex + 1;
%             addEdge = 1;
%         else
% %             msg = sprintf('Element %d already exists in mst node set', u);
% %             disp(msg);
%         end
% 
%         result2 = find(mstNodes == v);  % Check if the src node already exists in the mst node set
%         [row col] = size(result2);
%         if (col == 0)
% %             msg = sprintf('Node %d does not exist in mst node set...adding', v);
% %             disp(msg);
%             mstNodes(mstNodeIndex) = v;
%             mstNodeIndex = mstNodeIndex + 1;
%             addEdge = 1;
%         else
% %             msg = sprintf('Element %d already exists in mst node set', v);
% %             disp(msg)
%         end


