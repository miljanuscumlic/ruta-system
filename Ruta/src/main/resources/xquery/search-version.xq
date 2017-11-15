xquery version "3.1";

declare variable $path := '/db/ruta/system/version';
declare variable $since as xs:dateTime := xs:dateTime("2017-11-09T09:33:00");

(for $doc in xmldb:find-last-modified-since(collection($path)/*, $since)
 order by xmldb:last-modified($path, util:document-name($doc)) descending
 return $doc)[1]