xquery version "3.1";

declare variable $path external; (: := '/db/test/xquery/ruta-client/business-party'; :)
declare variable $following external := ();
declare variable $partner external := ();
declare variable $other external := ();
declare variable $archived external := ();
declare variable $deregistered external := ();

if($partner)
    then
        collection($path)[equals(.//Partner, 'true')]
else if($other)
    then
        collection($path)[equals(.//Following, 'true')][equals(.//Partner, 'false')]
else if($following)
    then
        collection($path)[equals(.//Following, 'true')]
else if($archived)
    then
        collection($path)[equals(.//Archived, 'true')]
else if($deregistered)
    then
        collection($path)[equals(.//Deregistered, 'true')]
    else
        ()