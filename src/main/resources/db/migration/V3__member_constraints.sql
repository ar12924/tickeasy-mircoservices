-- Unique constraints and index for MEMBER table
ALTER TABLE MEMBER
  ADD UNIQUE KEY UK_MEMBER_USERNAME (USER_NAME),
  ADD UNIQUE KEY UK_MEMBER_EMAIL (EMAIL);

-- Index for photo key (frequently queried for photo retrieval)
CREATE INDEX IDX_MEMBER_PHOTO_KEY ON MEMBER (PHOTO_KEY);


