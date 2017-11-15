xquery version "3.1";

declare namespace ns1 = 'urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2';
declare namespace ns2 = 'urn:oasis:names:specification:ubl:schema:xsd:CommonExtensionComponents-2';
declare namespace ns3 = 'urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2';
declare namespace ns4 = 'urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2';

declare variable $party-name external := ();
declare variable $party-company-id external := ();
declare variable $party-class-code external := ();
declare variable $party-city external := ();
declare variable $party-country external := ();
declare variable $party-all external := true();

declare variable $item-name external := ();
declare variable $item-description external := ();
declare variable $item-barcode external := ();
declare variable $item-comm-code external := ();
declare variable $item-keyword external := ();
declare variable $item-all external := true();

declare function local:transform($nodes as node()*) as item()* 
{
    for $node in $nodes
    return 
        typeswitch($node)
            case element(ns3:CatalogueLine)
                return local:transform-catalogue-line($node)
            case element(ns3:ReceiverParty)
                return ()
            case element(ns4:Catalogue) 
                return element{node-name($node)} {local:transform($node/node())}
            default return $node
};

declare function local:transform-catalogue-line($line as node()) as item()*
{
	let $item := $line/ns3:Item
	let $name := $item/ns1:Name
	let $description := $item/ns1:Description
	let $barcode := $item/ns3:SellersItemIdentification/ns1:BarcodeSymbologyID
	let $comm-code := $item/ns3:CommodityClassification/ns1:CommodityCode
	let $keyword := $item/ns1:Keyword
	return 
		if($item-all) then
			$line
			[(exists($item-name) and matches($name, $item-name, 'i') or not(exists($item-name))) and
			(exists($item-description) and matches($description, $item-description, 'i') or not(exists($item-description))) and
			(exists($item-barcode) and matches($barcode, $item-barcode, 'i') or not(exists($item-barcode))) and
			(exists($item-comm-code) and matches($comm-code, $item-comm-code, 'i') or not(exists($item-comm-code))) and
			(exists($item-keyword) and matches($keyword, $item-keyword, 'i') or not(exists($item-keyword)))]
		else
			$line[($item-name) and matches($name, $item-name, 'i') or
			exists($item-description) and matches($description, $item-description, 'i') or
			exists($item-barcode) and matches($barcode, $item-barcode, 'i') or 
			exists($item-comm-code) and matches($comm-code, $item-comm-code, 'i') or
			exists($item-keyword) and matches($keyword, $item-keyword, 'i')]
};

declare function local:filter-by-party($catalogues as node()*) as item()*
{
	for $catalogue in $catalogues
	let $party := $catalogue//ns3:ProviderParty
	let $name := $party/ns3:PartyName/ns1:Name
	let $company-id : = $party/ns3:PartyLegalEntity/ns1:CompanyID
	let $city := $party/ns3:PartyLegalEntity/ns3:RegistrationAddress/ns1:CityName
	let $class-code := $party/ns1:IndustryClassificationCode
	let $country := $party/ns3:PartyLegalEntity/ns3:RegistrationAddress/ns3:Country/ns1:Name
	
	return
	    if($party-all) then
	        $catalogue
	        [(exists($party-name) and matches($name, $party-name, 'i') or not(exists($party-name))) and
	        (exists($party-company-id) and matches($company-id, $party-company-id, 'i') or not(exists($party-company-id))) and
	        (exists($party-city) and matches($city, $party-city, 'i') or not(exists($party-city))) and
	        (exists($party-class-code) and matches($class-code, $party-class-code, 'i') or not(exists($party-class-code))) and
	        (exists($party-country) and matches($country, $party-country, 'i') or not(exists($party-country)))] 
	    else 
	        $catalogue
	        [exists($party-name) and matches($name, $party-name, 'i') or
	        exists($party-company-id) and matches($company-id, $party-company-id, 'i') or
	        exists($party-class-code) and matches($class-code, $party-class-code, 'i') or
	        exists($party-city) and matches($city, $party-city, 'i') or
	        exists($party-country) and matches($country, $party-country, 'i')]
};

declare function local:filter-by-item($catalogues as node()*) as item()*
{
	for $catalogue in local:transform($catalogues)
	return
		if(exists($catalogue/ns3:CatalogueLine)) then
			$catalogue
		else
			()
};

(: Deprecated :)
declare function local:filter-by-item-old($catalogues as node()*) as item()*
{
	for $item in $catalogues/ns3:CatalogueLine/ns3:Item   
	let $name := $item/ns1:Name
	let $description := $item/ns1:Description
	let $barcode := $item/ns3:SellersItemIdentification/ns1:BarcodeSymbologyID
	let $comm-code := $item/ns3:CommodityClassification/ns1:CommodityCode
	let $keyword := $item/ns1:Keyword
	let $filtered-by-item :=
	    if($item-all) then
	        $catalogues
	        [(exists($item-name) and matches($name, $item-name, 'i') or not(exists($item-name))) and
	        (exists($item-description) and matches($description, $item-description, 'i') or not(exists($item-description))) and
	        (exists($item-barcode) and matches($barcode, $item-barcode, 'i') or not(exists($item-barcode))) and
	        (exists($item-comm-code) and matches($comm-code, $item-comm-code, 'i') or not(exists($item-comm-code))) and
	        (exists($item-keyword) and matches($keyword, $item-keyword, 'i') or not(exists($item-keyword)))]
	    else
	        $catalogues
	        [exists($item-name) and matches($name, $item-name, 'i') or
	        exists($item-description) and matches($description, $item-description, 'i') or
	        exists($item-barcode) and matches($barcode, $item-barcode, 'i') or 
	        exists($item-comm-code) and matches($comm-code, $item-comm-code, 'i') or
	        exists($item-keyword) and matches($keyword, $item-keyword, 'i')]
	return
	 local:transform($filtered-by-item)
};

let $catalogues := 
    if (exists($party-name) or exists($party-company-id) or exists($party-class-code) or
        exists($party-city) or exists($party-country)) then
        local:filter-by-party(collection('/db/ruta/catalogue')/*)
    else
        collection('/db/ruta/catalogue')/*
return
	local:filter-by-item($catalogues)