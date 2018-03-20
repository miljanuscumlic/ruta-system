xquery version "3.1";

declare namespace ns3 = 'urn:rs:ruta:common';

declare variable $path external := ();

declare function local:transform($nodes as node()*) as item()* 
{
    for $node in $nodes
    return 
        typeswitch($node)
            case element(ID) | element(Summary) | element(Status) |
                element(Component) | element(Modified) | element(Version)
                return $node
            case element(ns3:BugReport) 
                return element{node-name($node)} {local:transform($node/node())}
            default return ()
};

local:transform(collection($path)/*)