# UserTestContract of family/tree project

## createUserWithInvalidEmail contract

1. contact func createUserWithInvalidEmail should be extended with new param: vararg errs: Pair<Error, Field>
2. Pair (EMAIL_PATTERN to Field.SSS_USER_EMAIL) should be passed in contract test
3. func createUserWithInvalidEmail should be modified in UserRestContractTest (with checking expected Error)
4. func createUserWithInvalidEmail should be implemented in UserGrpcContractTest (in similar REST way )   
5. Modified UserRestContractTests, UserGrpcContractTests must be executed successfully
 



