xquery version "3.1";

declare namespace ns2 = 'urn:rs:ruta:services';

declare variable $path external;

doc($path)//DocumentID/text()