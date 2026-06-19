# TODO

## Fix duplicate email causing 500 on student creation

- [x] Update studentRepository with email lookup (optional/existence method)
- [x] Update studentService.createStudent() to normalize email and reject duplicates before save
- [x] Update studentController to return 409 Conflict with message on duplicate email

- [ ] (Optional) Add global exception handler if preferred
- [x] Test POST /api/students with an existing email

## Fix circular JSON recursion between Department and Student

- [x] Add Jackson Managed/Back reference to stop infinite Department↔Student nesting.


