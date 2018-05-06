xquery version "3.1";

declare namespace ns1 = "http://www.ruta.rs/ns/common";
declare variable $path external;

doc($path)//ns1:DocumentID/text()