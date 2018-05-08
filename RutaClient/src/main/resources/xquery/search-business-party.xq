xquery version "3.1";

declare namespace ns1 = "http://www.ruta.rs/ns/client"; 
declare variable $path external; (: := '/db/test/xquery/ruta-client/business-party'; :)
declare variable $following external := ();
declare variable $partner external := ();
declare variable $other external := ();
declare variable $archived external := ();
declare variable $deregistered external := ();

if($partner)
    then
        collection($path)[equals(.//ns1:Partner, 'true')]
else if($other)
    then
        collection($path)[equals(.//ns1:Following, 'true')][equals(.//ns1:Partner, 'false')]
else if($following)
    then
        collection($path)[equals(.//ns1:Following, 'true')]
else if($archived)
    then
        collection($path)[equals(.//ns1:Archived, 'true')]
else if($deregistered)
    then
        collection($path)[equals(.//ns1:Deregistered, 'true')]
    else
        ()