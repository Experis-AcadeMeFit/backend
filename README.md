### API-08: Default Application Responses
The application server should have reasonable responses where the preceding specifications are incomplete:
- Most successful requests should return either 200 OK, 201 Created or 204 No
Content.
- All instances where a database record is created should return 201 Created, with
the location of the created resource.
- All instances where the input validation fails or malformed data is passed to an
endpoint should return 400 Bad Request.
- All instances where an unauthenticated user is attempting to access endpoints that
are strictly authorized (i.e. anything to do with contributor only requests) should
return 401 Unauthorized, even if the requested resources don’t exist.
- All instances where the user is authenticated but not authorized to view a partic12ular resource should return 403 Forbidden.
- All instances where requests are made to unknown endpoints should return 404
Not Found.
- Any request where the server enters an error state should fail immediately, without
exiting the server process, and return 500 Internal Server Error. While in
development, error messages may be included in the response but once deployed
the response should not contain any further information. In both cases, the error
should also be written to the application log for later debug and analysis.
- Other HTTP response codes may be used where applicable.