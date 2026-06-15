# UserTestContract of family/tree project

## UserTestContract update

1. Function createUserWithInvalidLogin (UserTestContract) should stand as not no-op operation (Unit result should be deleted)
2. createUserWithInvalidLogin should be extended with following parameters: 

   - login: String, 
   - email: String, 
   - password: String
   - vararg errs: Pair<Error, Field>

3. func createUserWithInvalidLogin in UserRestContractTests should be implemented as test in similar way (should test user creation with expected error(s)) 
4. Modified UserRestContractTests, UserGrpcContractTests must be executed successfully
 



