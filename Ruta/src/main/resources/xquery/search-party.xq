xquery version "3.1";

declare namespace ns1 = 'urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2';
declare namespace ns2 = 'urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2';

declare variable $party-name external := ();
declare variable $party-company-id external := ();
declare variable $party-class-code external := ();
declare variable $party-city external := ();
declare variable $party-country external := ();
declare variable $party-all external := true();

for $party in collection('/db/ruta/party')/*
let $name := $party/ns2:PartyName/ns1:Name
let $company-id : = $party/ns2:PartyLegalEntity/ns1:CompanyID
let $city := $party/ns2:PartyLegalEntity/ns2:RegistrationAddress/ns1:CityName
let $class-code := $party/ns1:IndustryClassificationCode
let $country := $party/ns2:PartyLegalEntity/ns2:RegistrationAddress/ns2:Country/ns1:Name
return
    if($party-all)
        then
            $party
            [(exists($party-name) and matches($name, $party-name, 'i') or not(exists($party-name))) and
            (exists($party-company-id) and matches($company-id, $party-company-id, 'i') or not(exists($party-company-id))) and
            (exists($party-city) and matches($city, $party-city, 'i') or not(exists($party-city))) and
            (exists($party-class-code) and matches($class-code, $party-class-code, 'i') or not(exists($party-class-code))) and
            (exists($party-country) and matches($country, $party-country, 'i') or not(exists($party-country)))] 
        else 
            $party
            [exists($party-name) and matches($name, $party-name, 'i') or
            exists($party-company-id) and matches($company-id, $party-company-id, 'i') or
            exists($party-class-code) and matches($class-code, $party-class-code, 'i') or
            exists($party-city) and matches($city, $party-city, 'i') or
            exists($party-country) and matches($country, $party-country, 'i')] 