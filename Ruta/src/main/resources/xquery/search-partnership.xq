xquery version "3.1";

declare namespace ns1 = "http://www.ruta.rs/ns/common";
declare namespace ns2 = "urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2";
declare namespace ns3 = "urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2";

declare variable $path external; (: := '/db/ruta-develop/partnership-request'; :)
declare variable $requester-id  external; (:  := '4d8a50e9-70ae-4b94-8dce-87bf57e5afc0'; :)
declare variable $requested-id external; (: := '2a69b053-0846-4c61-8eb6-dc5e53defd4a'; :)

collection($path)[(equals(.//ns1:RequesterParty/ns3:PartyIdentification/ns2:ID, $requester-id) and
equals(.//ns1:RequestedParty/ns3:PartyIdentification/ns2:ID, $requested-id)) or
(equals(.//ns1:RequesterParty/ns3:PartyIdentification/ns2:ID, $requested-id) and
equals(.//ns1:RequestedParty/ns3:PartyIdentification/ns2:ID, $requester-id))]