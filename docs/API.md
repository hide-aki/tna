**Search and Pagination**
    
   - `search`: [field]=='value'. RSQL parser link
   - `page`: zero indexed page number
   - `size`: 10, Page Size
   - `sort`: [field],asc|desc. e.g- ?sort=name,desc
   
    combined example
    ?search=name=='*Dress*'&page=0&sort=id,desc

**FetchAll Response Structure**

```$xslt
{
	"content": [{
		"id": 5,
		"modifiedAt": 1576391224503,
		"lastModifiedBy": "super_user",
		"name": "Muji",
		"code": "MUJI",
		"desc": null,
		"uid": "Buyer-Muji"
	}],
	"last": true,
	"totalPages": 1,
	"totalElements": 5,
	"size": 10,
	"first": true,
	"number": 0,
	"sort": [{
		"direction": "DESC",
		"property": "id",
		"ignoreCase": false,
		"nullHandling": "NATIVE",
		"ascending": false,
		"descending": true
	}],
	"numberOfElements": 5
}
```

**Buyer API**

1. Fetch All

    ```$xslt
    url: ~/v1/api/buyers
    method: GET
    action: 
    params: search, page, size, sort
    request data: 
    response data: 
        data structure - 
            {
                "id": 5,
                "name": "Muji",
                "desc": null,
            }

```


| Endpoint | method | action | params | data    | Description|
| ---      | ---    | ---    | ---    | ---     | ---        |
| ~/v1/api/buyers | GET   | |search, page, sort | |  fetch all buyers    |
| ~/v1/api/buyers/{buyerId} | GET   | | |  | fetch one buyer    |
| ~/v1/api/buyers | POST   | | | buyer data in JSON | create new buyer    |
| ~/v1/api/buyers/{buyerId} | PUT   | | | buyer data in JSON | update one buyer   |
| ~/v1/api/buyers/{buyerId} | DELETE   | | |  | delete one buyer    |

**Season API**

| Endpoint | method | action | params | data    | Description|
| ---      | ---    | ---    | ---    | ---     | ---        |
| ~/v1/api/seasons | GET   | |search, page, sort | |  fetch all seasons    |
| ~/v1/api/seasons/{seasonId} | GET   | | |  | fetch one season    |
| ~/v1/api/seasons | POST   | | | season data in JSON | create new season    |
| ~/v1/api/seasons/{seasonId} | PUT   | | | season data in JSON | update one season   |
| ~/v1/api/seasons/{seasonId} | DELETE   | | |  | delete one season    |

**Department API**

| Endpoint | method | action | params | data    | Description|
| ---      | ---    | ---    | ---    | ---     | ---        |
| ~/v1/api/departments | GET   | |search, page, sort | |  fetch all departments    |
| ~/v1/api/departments/{departmentId} | GET   | | |  | fetch one department    |
| ~/v1/api/departments | POST   | | | department data in JSON | create new department    |
| ~/v1/api/departments/{departmentId} | PUT   | | | department data in JSON | update one department   |
| ~/v1/api/departments/{departmentId} | DELETE   | | |  | delete one department    |

**Team API**

| Endpoint | method | action | params | data    | Description|
| ---      | ---    | ---    | ---    | ---     | ---        |
| ~/v1/api/teams | GET   | |search, page, sort | |  fetch all teams    |
| ~/v1/api/teams/{teamId} | GET   | | |  | fetch one team    |
| ~/v1/api/teams | POST   | | | team data in JSON | create new team    |
| ~/v1/api/teams/{teamId} | PUT   | | | team data in JSON | update one team   |
| ~/v1/api/teams/{teamId} | DELETE   | | |  | delete one team    |

**Garment Type API**

| Endpoint | method | action | params | data    | Description|
| ---      | ---    | ---    | ---    | ---     | ---        |
| ~/v1/api/garmentTypes | GET   | |search, page, sort | |  fetch all garmentTypes    |
| ~/v1/api/garmentTypes/{garmentTypId} | GET   | | |  | fetch one garmentType    |
| ~/v1/api/garmentTypes | POST   | | | garmentType data in JSON | create new garmentType    |
| ~/v1/api/garmentTypes/{garmentTypId} | PUT   | | | garmentType data in JSON | update one garmentType   |
| ~/v1/api/garmentTypes/{garmentTypId} | DELETE   | | |  | delete one garmentType    |
