SELECT MEMBER_ID, MEMBER_TYPE
FROM package_component_members
WHERE component_id = ?
AND inactive_dt IS NULL