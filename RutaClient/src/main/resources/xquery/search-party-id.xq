xquery version "3.1";

declare namespace ns1 = "http://www.ruta.rs/ns/common";
declare variable $path external;(: := '/db/ruta-develop/system/party-id/'; :)
declare variable $id external; (: = 'b5569acf-c0ba-49ed-ac5c-4c5f13d1d4e7'; :)

let $res := collection($path)[equals(.//ns1:DocumentID,$id)]
return substring-before(util:document-name($res), '.xml')