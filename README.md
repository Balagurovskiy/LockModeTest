# LockModeTest
Example for transaction lock usage
#
Simple project to check optimistic/pesimistic locks. There is simple db (<a href="library_init.sql"> create sql </a>) with authors, book and tags with many-to-many relationships.
Unit test will create several select and update concurrent transactions with different lock mode types.
