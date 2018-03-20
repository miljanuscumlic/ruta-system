xquery version "3.1";

declare variable $path external := (); (:'/db/ruta-develop/doc-box/4261a479-1974-419f-a44f-6b2d5f1390a2';:)

for $doc in xmldb:xcollection($path)
order by xmldb:last-modified($path, util:document-name($doc)) ascending
return 
    substring-before(util:document-name($doc), '.xml')