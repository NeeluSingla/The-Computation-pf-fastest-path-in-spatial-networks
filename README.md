I have implemented both the heuristic functions using A* Algorithm.
List<MyNodeInfo> pointList = routingAStarWithTimeCost is the name of the function which calculates the path cost using the Ecludian distance.
List<MyNodeInfo> pointList = routingAStarForwardingWithTimeCost is the function that calculates the path cost using the node to border distances.


There are a couple of nodes that bring out different values for the distances:
n0 n546223 20:12:11 80
n100006 n677544 20:12:11 80

In order to bring out the same results for both the heuristic functions I have divided the second heuristic function with a value 1000.That is the the addition of nb+bb+bn/1000
It then calculates roughly the same path cost for both the heuristic functions.
I still cannot place the exact reason as to why does it work,but it can be assumed as conversion to kms and then calculated.
