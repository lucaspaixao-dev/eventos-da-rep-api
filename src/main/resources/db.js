db.createUser(
{
  user: "admin",
  pwd: "1234",
  roles: [
    {
      role: "readWrite",
      db: "eventos_da_rep"
    }
  ]
}
);