xquery version "3.1";

declare variable $path external; (: = '/db/ruta-develop/followers'; :)
declare variable $follower-id external; (: = '4a2a4f66-653d-44f6-b630-49f99c5ef289'; :)

collection($path)[equals(.//FollowerID, $follower-id)]